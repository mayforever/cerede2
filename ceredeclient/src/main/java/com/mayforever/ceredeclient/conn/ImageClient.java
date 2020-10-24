/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.conn;

import com.mayforever.cerede.protocol.AccessRequest;
import com.mayforever.cerede.protocol.ImageRequest;
import com.mayforever.cerede.protocol.ImageResponse;
import com.mayforever.ceredeclient.App;
import com.mayforever.ceredeclient.RemoteViewer;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import com.mayforever.tools.BitConverter;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.ByteOrder;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import com.mayforever.cerede.rmi.InterfaceImage;

/**
 *
 * @author mis
 */
public class ImageClient extends BaseThread 
        implements com.mayforever.network.newtcp.ClientListener{
    private com.mayforever.network.newtcp.TCPClient tcpClient = null;
    Logger logger = null;
    private byte CONTROL = 0;

    public Robot getRobot() {
        return robot;
    }
    private Robot robot = null;
    private byte[] tempData = null;
    private Queue<byte[]> dataProcess = null;
    private Queue<byte[]> dataToValidate = null;
    private BufferedImage oldBufferImage = null;

    public void setOldBufferImage(BufferedImage oldBufferImage) {
        this.oldBufferImage = oldBufferImage;
    }
//    private float resizeValue = 1.5f;
//    private ArrayList<byte[]> sendArraylistImage=null;
//    private SessionRecieveMonitor sessionCleaner = null;
//    private HashMap<String, byte[]> mapTempData = null;
//    private HashMap<String, ArrayList<byte[]>> mapSendImageArrayList = null;
//    private HashMap<String, byte[]> mapRecieverBufferImage = null;
    private long width;
    private long height;
    private Rectangle rectangle = null;
    public ImageClient(){
//        mapSendImageArrayList = new HashMap<>();
        logger = Logger.getLogger("IMAGECLIENT");
        boolean isAlive = false;
        while(!isAlive){
            try{
            
                this.tcpClient = new com.mayforever.network.newtcp.TCPClient(App.serverIP, App.serverPort);
                this.tcpClient.addListener(this);
                logger.debug("server reconnected sucessfuly");
                isAlive = true;
            }catch (NullPointerException e){
                logger.debug("reconecting to server");
//                App.imageClient = new ImageClient();
                
                try {
                    java.lang.Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    logger.warn("ERROR :"+ex.getMessage());
                    ex.printStackTrace();
                }

            }
        }
        
//        this.tcpClient.setAllocation(2048*5);
        dataProcess = new Queue<>();
        dataToValidate = new Queue<>();
        new Processor();
//        mapRecieverBufferImage = new HashMap<>();
        logger.debug("the authentication has been sent");
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        try {
                robot = new Robot(graphicsDevice);
                rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                this.width = (long)(Math.ceil(rectangle.width/App.resizeValue));
                this.height = (long)(Math.ceil(rectangle.height/App.resizeValue));
        } catch (AWTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
        
        this.startThread();
        
        this.sendAuthentication();
//        sessionCleaner=new SessionRecieveMonitor();
//        sessionCleaner.startSessionCleaner();
    }
    
    @Override
    public void packetData(byte[] data) {

	this.dataToValidate.add(data);
    }

    @Override
    public void socketError(Exception excptn) {
            this.stopThread();

            App.imageClient = new ImageClient();
            excptn.printStackTrace();
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
    
    public void sendImagePacket (byte[] data){
        try {
            this.tcpClient.sendPacket(data);
            logger.debug("data has been sent : "+data.length);
        } catch (IOException ex) {
           logger.error(ex.getMessage());
           logger.error(ex.getClass());
        }
    }

    @Override
    public void run() {
        while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
            byte[] data = null;
            try {
                data = dataToValidate.get();
                if (data != null) {

                    if (tempData == null || tempData.length  == 0) {
			tempData = data;
                    }else {

                            byte[] dataPending = tempData;
                            tempData = new byte[tempData.length + data.length];
                            System.arraycopy(dataPending, 0, tempData, 0, dataPending.length);
                            System.arraycopy(data, 0, tempData, dataPending.length, data.length);
                    }

                    logger.debug(tempData.length);
                    int dataProcessSize = BitConverter.bytesToInt(tempData, 1, ByteOrder.BIG_ENDIAN);

                    logger.debug(dataProcessSize);
		
                    do {
			if(dataProcessSize == tempData.length) {
				this.dataProcess.add(tempData);
				tempData = new byte[0];
			}else if(dataProcessSize < tempData.length) {
				byte[] dataToProcess = new byte[dataProcessSize];
				System.arraycopy(tempData, 0, dataToProcess, 0, dataProcessSize);
				this.dataProcess.add(dataToProcess);
				byte[] newtempData = new byte[tempData.length - dataProcessSize];
				System.arraycopy(tempData, dataProcessSize, newtempData, 0,  tempData.length - dataProcessSize);
				this.tempData = newtempData;
				logger.debug("newtempData length : " + newtempData.length);
			}
			logger.debug("tempData length : " + tempData.length);
			if(this.tempData.length < 5) {
				break;
			}else {
				dataProcessSize = BitConverter.bytesToInt(tempData, 1, ByteOrder.BIG_ENDIAN);
			}
			logger.debug("dataProcessSize length : " + dataProcessSize );
                        if(tempData == null){
                            logger.warn("tempData is empty");
                        }
                    }while(tempData.length > dataProcessSize);
                }
            } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
        }
    }
    
     public BufferedImage getScreen() {
        BufferedImage img = robot.createScreenCapture(rectangle);
        int newW = (int) (Math.ceil(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/App.resizeValue));
        int newH = (int) (Math.ceil(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/App.resizeValue));
        Image tmp = img.getScaledInstance(newW,newH,Image.SCALE_FAST);
        BufferedImage dimg = new BufferedImage(newW,newH,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp,0,0,null);
        g2d.dispose();
        return dimg;
    }
    
    public BufferedImage gettingScreenShot(){
//		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage capture = this.getScreen();
//		capture = robot.createScreenCapture(rectangle);
		
		return capture;
	}
    
    public HashMap<Short, HashMap<Short, Integer>> getBlockChanges(BufferedImage oldImageBuff
            ,BufferedImage newImageBuff){
//        Graphics2D graphics2d = newImageBuff.createGraphics();
        HashMap<Short, HashMap<Short, Integer>> tempBlockChanges = new HashMap<>();
        if(oldImageBuff != null){
//            Graphics2D oldGraphics2D = oldImageBuff.createGraphics();
            int[][] oldImageArray = new int[(int)width][(int)height];
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    oldImageArray[i][j] = oldImageBuff.getRGB(i, j);
                }
            }
            
            int[][] newImageArray = new int[(int)width][(int)height];
            for(int i = 0; i < width; i++){
                for(int j = 0; j < height; j++){
                    newImageArray[i][j] = newImageBuff.getRGB(i, j);
                }
            }
            
            for(int i = 0; i < width; i++){
                HashMap<Short,Integer> xy = new HashMap<>();
                for(int j = 0; j < height; j++){
                    if(oldImageArray[i][j] != newImageArray[i][j]){
                        xy.put((short)j,  newImageBuff.getRGB(i, j));
                    }
                    
                }
                if(!xy.isEmpty())
                        tempBlockChanges.put((short)i, xy);
            }
            
        }else{
//            int[][] newImageArray = new int[(int)width][(int)height];
            for(int i = 0; i < width; i++){
                HashMap<Short,Integer> xy = xy = new HashMap<>();
                for(int j = 0; j < height; j++){
                    
                    xy.put((short)j, newImageBuff.getRGB(i, j));
                    
                }
                if(!xy.isEmpty())
                    tempBlockChanges.put((short)i, xy);
            }
        }
        this.oldBufferImage = newImageBuff;
//        System.out.print("Finish getting graphics changes");
        return tempBlockChanges;
    }
//    public byte[] gettingScreenShot(){
//		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
//		BufferedImage capture = null;
//		capture = robot.createScreenCapture(screenRect);
//		// System.out.
//                byte[] bufferImage = null;
//		if(capture!=null){
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			byte[] baosArrays = null;
//			try {
//				ImageIO.write(capture, "png", baos);
//				baos.flush();
//
//				bufferImage = baos.toByteArray();
//                                baos.close();
////                                baosArrays = baos.toByteArray();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			return bufferImage;
//		}
//		return null;
//	}
    
    public ArrayList<byte[]> chunckData(byte[] data, int chunkCount){
    	int dataCount = data.length;
    	ArrayList<byte[]> chunkdata = new ArrayList<byte[]>(); 

    	int lengthPerChunk = Math.floorDiv(dataCount ,chunkCount);
    	int indexOfData = 0;
    	for(int i = 1; i <= chunkCount ; i++) {
    		if(chunkCount ==i) {
    			byte[] dataChunk = new byte[data.length - indexOfData + 5];
    			System.arraycopy(data, indexOfData, dataChunk, 0, lengthPerChunk);
    			indexOfData+=lengthPerChunk;

    			chunkdata.add(dataChunk);
    		}else {
    			byte[] dataChunk = new byte[lengthPerChunk];
    			System.arraycopy(data, indexOfData, dataChunk, 0, lengthPerChunk);
    			indexOfData+=lengthPerChunk;
;
    			chunkdata.add(dataChunk);
    		}
    		
    	}
    	return chunkdata;
    	
    }
    
    private class Processor extends BaseThread{
        Processor(){
            this.startThread();
        }
        @Override
        public void run() {
            while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
            byte[] data = null;
            try {
                data = dataProcess.get();
                if (data != null && data.length != 0) {
//                    System.out.println("data to process :" + Arrays.toString(data));
                    
                    if(data[0] == 2) {
                       
                        ImageRequest imageRequest = new ImageRequest();
                        imageRequest.fromBytes(data);
                        String imageKey = App.toHash(imageRequest.getRequestorHash()
                                +imageRequest.getHash());
                        
                        try {
                            logger.debug("the address was "+"//"+App.config.getServerAddress()+
                                ":"+App.config.getRmiServerPort()+"/cerede");
                            App.rmiClient = (InterfaceImage) Naming.lookup("//"+App.config.getServerAddress()+
                                ":"+App.config.getRmiServerPort()+"/cerede");
                            HashMap<Short, HashMap<Short, Integer>> blockChanged = getBlockChanges
                                        (oldBufferImage, gettingScreenShot());
                            App.rmiClient.addChangeColumns(imageKey, blockChanged);
                            logger.debug(blockChanged.size());
                        } catch (RemoteException ex) {
                            ex.printStackTrace();
                        } catch (NotBoundException ex) {
                            java.util.logging.Logger.getLogger(ImageClient.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MalformedURLException ex) {
                            java.util.logging.Logger.getLogger(ImageClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        ImageResponse imageResponse = new ImageResponse();
                        imageResponse.setRequestorHash(imageRequest.getRequestorHash());
                        imageResponse.setHash(imageRequest.getHash());
                        imageResponse.setResult((byte)0);
                        imageResponse.setHeight(getHeight());
                        imageResponse.setWidth(getWidth());
                        imageResponse.setProtocol((byte)3);
                        imageResponse.setResultString("ok");
                        
                        
                        byte[] dataToSend = null;
                        try {
                            dataToSend = imageResponse.toBytes();
                            tcpClient.sendPacket(dataToSend);
                            logger.debug("Message Response Send at 2 ");
                            logger.debug("data response result : "+ dataToSend.length);
                        } catch (IOException ex) {
                        
                        }
                    }else if(data[0] == 3){
                        ImageResponse imageResponse = new ImageResponse();
                        imageResponse.fromBytes(data);
                        logger.debug("image response to process");
                        String imageKey = App.toHash(imageResponse.getRequestorHash()
                                +imageResponse.getHash());
                        if(App.mapRemoteViewer.containsKey(imageResponse.getHash())){
                             
                            RemoteViewer remoteViewer = App.mapRemoteViewer.get(imageResponse.getHash());
                            
                            remoteViewer.updateJScrollViewSize(imageResponse);
                            try {
                                App.rmiClient = (InterfaceImage) Naming.lookup("rmi://"+App.config.getServerAddress()+
                                ":"+App.config.getRmiServerPort()+"/cerede");
                                HashMap<Short, HashMap<Short, Integer>> screenChanges
                                        = App.rmiClient.getChangeColumns(imageKey);
                                while(screenChanges.equals(null)){
                                    java.lang.Thread.sleep(1);
                                    screenChanges
                                        = App.rmiClient.getChangeColumns(imageKey);
                                }
                                    remoteViewer.setImageChanges(screenChanges);
                            
                                } catch (RemoteException ex) {
                                    ex.printStackTrace();
                                } catch (NotBoundException ex) {
                                java.util.logging.Logger.getLogger(ImageClient.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (MalformedURLException ex) {
                                java.util.logging.Logger.getLogger(ImageClient.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                          
                            }
                        
                        
                        
                    }else if(data[0] == 4){
                        
                    }else if(data[0] == 5){
                        
                    }
                }
            } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }catch (ArrayIndexOutOfBoundsException eiobe){
//                eiobe.printStackTrace();
                socketError(eiobe);
            }
        }
        }
        
        
    }
    
    private int getHeight(){
//        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
        return (int) this.height;
    }
    
    private int getWidth(){
//        Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
        return (int) this.width;
    }
    private long lastImageDataTimeProcess = 0l;
    private void updateLastSessionDate(){
        Date date = new Date();
        lastImageDataTimeProcess = date.getTime();
    }
//    private class SessionRecieveMonitor extends BaseThread{
//        SessionRecieveMonitor(){
//            
//        }
//        
//        public void startSessionCleaner(){
//            this.startThread();
//        }
//        
//        public void stopSessionCleaner(){
//            this.stopThread();
//        }
//        @Override
//        public void run() {
//            while(true){
//                try {
//                    java.lang.Thread.sleep(2000);
//                } catch (InterruptedException ex) {
//                    logger.warn("ERROR :"+ex.getMessage());
//                    ex.printStackTrace();
//                }
//                if(this.getServiceState() ==  com.mayforever.thread.state.ServiceState.RUNNING)
//                {
//                    
//                    Date date = new Date();
//                    long currentTime = date.getTime();
//                    long timeDiff = currentTime - lastImageDataTimeProcess;
//                    
//                    int diffsec = (int) (timeDiff / (1000));
//                    
//                    if(diffsec >= 5){
//                        tempData = null;
//                    }
//                }
//            }
//        }
//    }
}
