/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.conn;

import com.mayforever.cerede.protocol.AccessRequest;
import com.mayforever.cerede.protocol.ClipboardInvoke;
import com.mayforever.cerede.protocol.Command;
import com.mayforever.cerede.protocol.CommandRequest;
import com.mayforever.ceredeclient.App;
import com.mayforever.ceredeclient.device.ClipboardChanger;
import com.mayforever.network.newtcp.TCPClient;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import com.mayforever.tools.BitConverter;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import org.apache.log4j.Logger;
/**
 *
 * @author mis
 */


public class CommandClient  
    implements com.mayforever.network.newtcp.ClientListener{
    private com.mayforever.network.newtcp.TCPClient tcpClient = null;
    private ClipboardChanger clipboardChanger = null;
    public TCPClient getTcpClient() {
        return tcpClient;
    }
    Logger logger = null;
    private byte CONTROL = 1;
//    private byte[] tempData = null;
    private Queue<byte[]> dataProcess = null;
//    private Queue<byte[]> dataToValidate = null;
 
    public CommandClient(){
        boolean isAlive = false;
        logger = Logger.getLogger("COMMAND CLIENT");
        dataProcess = new Queue<>();
//        dataToValidate = new Queue<>();
        clipboardChanger = new ClipboardChanger(Toolkit.getDefaultToolkit().getSystemClipboard());
        while(!isAlive){
            try{
            
                this.tcpClient = new com.mayforever.network.newtcp.TCPClient(App.serverIP, App.serverPort);
                this.tcpClient.addListener(this);
                logger.info("server reconnected sucessfuly");
                isAlive = true;
            }catch (NullPointerException e){
                logger.info("reconecting to server");                
                try {
                    java.lang.Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    logger.warn("ERROR :"+ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
        
        
        this.sendAuthentication();
        logger.debug("the authentication has been sent");
        
        new Processor();
    }
    
    public void packetData(byte[] bytes) {
//        System.out.println(bytes.length);
//     
//        System.out.println("data :" + Arrays.toString(bytes));
        logger.debug(bytes.length);
        int dataProcessSize = BitConverter.bytesToInt(bytes, 1, ByteOrder.BIG_ENDIAN);
//        System.out.println("data :" + Arrays.toString(bytes));
        logger.debug(dataProcessSize);

        do {
            
            if(dataProcessSize == bytes.length) {
                    this.dataProcess.add(bytes);
                    bytes = new byte[0];
            }else if(dataProcessSize < bytes.length) {
//                    System.out.println("data :" + Arrays.toString(bytes));
                    dataProcessSize = BitConverter.bytesToInt(bytes, 1, ByteOrder.BIG_ENDIAN);
//                    System.out.println("tempData.length > dataPro " + bytes.length+">"+dataProcessSize);
                    byte[] dataToProcess = new byte[dataProcessSize];
                    System.arraycopy(bytes, 0, dataToProcess, 0, dataProcessSize);
                    this.dataProcess.add(dataToProcess);
                    byte[] newtempData = new byte[bytes.length - dataProcessSize];
                    System.arraycopy(bytes, dataProcessSize, newtempData, 0,  bytes.length - dataProcessSize);
                    bytes = newtempData;
                    logger.debug("newtempData length : " + newtempData.length);
            }
            logger.debug("tempData length : " + bytes.length);
            if(bytes.length < 5) {
//                 System.out.println("data break");
                 break;
            }else {    
                dataProcessSize = BitConverter.bytesToInt(bytes, 1, ByteOrder.BIG_ENDIAN);
                if(dataProcessSize == 0){
                    break;
                }
            }
                logger.debug("dataProcessSize length : " + dataProcessSize );
            if(bytes == null){
                logger.warn("tempData is empty");
            }
            
        }while(bytes.length >= dataProcessSize );
    }

    
    public void socketError(Exception excptn) {
        excptn.printStackTrace();
        App.commandClient = new CommandClient();
    }
    
    private void reconnect(){
        
    }
    
    private void sendAuthentication(){
        AccessRequest accessRequest = new AccessRequest();
        
        accessRequest.setUsername(App.username);
        accessRequest.setPassword(App.password);
        accessRequest.setControl(CONTROL);
        
        try {
            this.tcpClient.sendPacket(accessRequest.toBytes());
        } catch (IOException ex) {
           logger.error(ex.getMessage());
           logger.error(ex.getClass());
        }
    }

    public void sendClipboardInvoke(ClipboardInvoke clipboardInvoke) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public void run() {
//        while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
//            byte[] data = null;
//            try {
//                data = dataToValidate.get();
//                
//                
//                }
//            } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//            }
//        }
//    }
    
    private class Processor extends BaseThread{
        
        private Processor(){
            this.startThread();
        }
        
        public void run() {
            while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING){
                byte[] data = null;
                try {
                    data = dataProcess.get();
                    if(data != null && data.length > 0){
                        if  (data[0] == 6){     
                            CommandRequest commandRequest = new CommandRequest();
                            logger.debug(data.length);
                            commandRequest.fromBytes(data);
//                            if(commandRequest.getCommand() == 3){
//                                System.out.println("PRESSED : "+data);
//                            }
                            logger.debug(commandRequest.getTotalSize());
                            doEvent(commandRequest);    
                            logger.debug(Arrays.toString(data));
                        }else if (data[0] == 7){
                            logger.info("Clipboard Received");
                            ClipboardInvoke clipboardInvoke = new ClipboardInvoke();
                            clipboardInvoke.fromBytes(data);
                            
                            String requestorHash = clipboardInvoke.getRequestorHash();
                            String hash = clipboardInvoke.getHash();
                            String clipboardKey = App.toHash(requestorHash+hash);
                            
                            String cliboardText = App.rmiClient.getClipboard(clipboardKey);
                            clipboardChanger.updateClipboard(cliboardText);
                        }
                    }       
                } catch (InterruptedException ex) {
                    logger.warn("ERROR :"+ex.getMessage());
                    ex.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }  
        }
    }
    
    private void doEvent(CommandRequest commandRequest){
        byte command = (byte)999;
        try{
                command = commandRequest.getCommand();
        }catch (Exception e){
                System.out.println("failed to catch real command please repeat");
        }

        if(command == Command.MOUSE_PRESSED){
            App.imageClient.getRobot().mousePress(commandRequest.getParams()[0]);
        }else if (command == Command.MOUSE_RELEASED){
            App.imageClient.getRobot().mouseRelease(commandRequest.getParams()[0]);
        }else if (command == Command.MOUSE_MOVE){
            App.imageClient.getRobot().mouseMove((int) (commandRequest.getParams()[0] * App.resizeValue), 
                        (int) (commandRequest.getParams()[1] * App.resizeValue));
        }else if (command == Command.KEY_PRESSED){
            System.out.println("PRESSED : "+commandRequest.getParams()[0]);
//              System.out.println("Must be : "+KeyEvent.VK_TAB);
            App.imageClient.getRobot().keyPress(commandRequest.getParams()[0]);
        }else if (command == Command.KEY_RELEASED){
            System.out.println("RELEASED : "+commandRequest.getParams()[0]);
            App.imageClient.getRobot().keyRelease(commandRequest.getParams()[0]);
        }
    }
}
