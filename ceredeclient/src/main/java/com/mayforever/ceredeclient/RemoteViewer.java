/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient;

import com.mayforever.cerede.protocol.Command;
import com.mayforever.cerede.protocol.CommandRequest;
import com.mayforever.cerede.protocol.ImageRequest;
import com.mayforever.cerede.protocol.ImageResponse;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;

/**
 *
 * @author mis
 */
public class RemoteViewer extends javax.swing.JFrame 
    implements Runnable,MouseMotionListener,MouseListener,KeyListener, WindowListener{

    /**
     * Creates new form RemoteVIewer
     */
//    Image image1;
//    Graphics graphics;
    public Logger logger = Logger.getLogger("Remote Viewer");
    private LoadingFrame loadingFrame = null;
    private String hash = null;
    public int chunkIndex = 0;
    private long lastImageDataTimeProcess = 0l;
    private com.mayforever.queue.Queue<CommandRequest> queueCommandRequest = null;
    private CommandSender commandSender = null;
//    private SessionSendMonitor sessionSendMonitor = null;
    private BufferedImage oldBufferdImage = null;
    public RemoteViewer(String hash) {
 
        
        initComponents();
        
        loadingFrame = new LoadingFrame();
        logger.debug("Maximum loading frame progress :" + loadingFrame.getjProgressBar().getMaximum());
        logger.debug("Manimum loading frame progress :" + loadingFrame.getjProgressBar().getMinimum());
        queueCommandRequest = new Queue<>();
        commandSender = new CommandSender();
        this.hash = hash;
        this.jPanel1.addMouseListener(this);
        this.jPanel1.addMouseMotionListener(this);
        
        this.showRemoteViewer();
        
        this.addKeyListener(this);
        this.addWindowListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void run(){
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        this.loadingFrame.setVisible(true);
        this.loadingFrame.getjLprocess().setText("Sending Request To Server ...");
        logger.debug("Sending Request To Server ...");
        
        ImageRequest imageRequest = new ImageRequest();
        
        imageRequest.setHash(hash);
        imageRequest.setRequestorHash(App.hash);
        this.loadingFrame.getjLprocess().setText("Sending Image Request To Server ...");
        App.imageClient.sendImagePacket(imageRequest.toBytes());
        this.updateLastSessionDate();
//        sessionSendMonitor = new SessionSendMonitor();
        setVisible(true);
    }
    
    public void showRemoteViewer(){
        java.awt.EventQueue.invokeLater(this);
    }
    private int WIDTH = 0;
    private int HEIGHT = 0;
    
    int mouseX = 0; 
    int mouseY = 0;
    
    public void setImageChanges(HashMap<Short, HashMap<Short, Integer>> changesMap){
//        boolean noOldBuffer = 
        if(oldBufferdImage== null){
             oldBufferdImage = new BufferedImage((int)(WIDTH/1.5),(int)( HEIGHT/1.5), Image.SCALE_FAST);
        }
        for(Map.Entry<Short, HashMap<Short, Integer>> entry : changesMap.entrySet()) {
            int columnkey = entry.getKey();
            HashMap<Short, Integer> value = entry.getValue();
            for(Map.Entry<Short, Integer> entry1 : value.entrySet()){
                int rowKey = entry1.getKey();
                int changes = entry1.getValue();
                oldBufferdImage.setRGB(columnkey, rowKey, changes);
            }
        }
        this.updateJScrollView(oldBufferdImage);
//        System.out.print("Finish edting graphics");
    }
    public void updateJScrollView(BufferedImage bufferImage){
            
        bufferImage.getScaledInstance(WIDTH,HEIGHT,Image.SCALE_FAST);
        Graphics graphics = jPanel1.getGraphics();
        graphics.drawImage(bufferImage, 0, 0, WIDTH,HEIGHT, jPanel1);
        jPanel1.validate();
        this.updateLastSessionDate();
           
        ImageRequest imageRequest = new ImageRequest();
        
        imageRequest.setHash(hash);
        imageRequest.setRequestorHash(App.hash);
        App.imageClient.sendImagePacket(imageRequest.toBytes());
    }
    
    public void updateJScrollViewSize(ImageResponse imageResponse){
        logger.debug("Processing image Response...");
        if((WIDTH != imageResponse.getWidth()) && (HEIGHT != imageResponse.getHeight())){
            getjPanel1().setPreferredSize(new Dimension(imageResponse.getWidth(),imageResponse.getHeight()));
            WIDTH = imageResponse.getWidth();
            HEIGHT = imageResponse.getHeight();
            
            logger.info("device size set "+WIDTH+"x"+HEIGHT );
            getjScrollPane1().setViewportView(getjPanel1());
            getjScrollPane1().validate();
        }
    }

    

    public JPanel getjPanel1() {
        return jPanel1;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
        mouseX = e.getX();
        mouseY = e.getY();
//        System.out.println(InputEvent.getMaskForButton(e.getButton()));
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setProtocol((byte)6);
        commandRequest.setCommand(Command.MOUSE_MOVE);
        commandRequest.setHash(hash);
        commandRequest.setRequestorHash(App.hash);
        int[] params = {mouseX, mouseY};
        commandRequest.setParams(params);
        this.queueCommandRequest.add(commandRequest);
//        try {
//            App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//
//        } catch (IOException ex) {
//            logger.warn("ERROR :"+ex.getMessage());
//            ex.printStackTrace();
//        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
        mouseX = e.getX();
        mouseY = e.getY();
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setProtocol((byte)6);
        commandRequest.setCommand(Command.MOUSE_MOVE);
        commandRequest.setHash(hash);
        commandRequest.setRequestorHash(App.hash);
        int[] params = {mouseX, mouseY};
        commandRequest.setParams(params);
        this.queueCommandRequest.add(commandRequest);
//        try {
//            App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//
//        } catch (IOException ex) {
//            logger.warn("ERROR :"+ex.getMessage());
//            ex.printStackTrace();
//        }
    }
    

    @Override
    public void mouseClicked(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setProtocol((byte)6);
        commandRequest.setCommand(Command.MOUSE_PRESSED);
        commandRequest.setHash(hash);
        commandRequest.setRequestorHash(App.hash);
        int[] params = {InputEvent.getMaskForButton(e.getButton())};
        commandRequest.setParams(params);
        this.queueCommandRequest.add(commandRequest);
//        try {
//            App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//
//        } catch (IOException ex) {
//            logger.warn("ERROR :"+ex.getMessage());
//            ex.printStackTrace();
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
        CommandRequest commandRequest = new CommandRequest();
        commandRequest.setProtocol((byte)6);
        commandRequest.setCommand(Command.MOUSE_RELEASED);
        commandRequest.setHash(hash);
        commandRequest.setRequestorHash(App.hash);
        int[] params = {InputEvent.getMaskForButton(e.getButton())};
        commandRequest.setParams(params);
        this.queueCommandRequest.add(commandRequest);
//        try {
//            App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//
//        } catch (IOException ex) {
//            logger.warn("ERROR :"+ex.getMessage());
//            ex.printStackTrace();
//        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //To change body of generated methods, choose Tools | Templates.
//        CommandRequest commandRequest = new CommandRequest();
//        commandRequest.setProtocol((byte)6);
//        commandRequest.setCommand(Command.KEY_TYPED);
//        commandRequest.setHash(hash);
//        commandRequest.setRequestorHash(App.hash);
//        int[] params = {e.getExtendedKeyCode()};
//        commandRequest.setParams(params);
//        try {
//            
//            App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//
//        } catch (IOException ex) {
//            logger.warn("ERROR :"+ex.getMessage());
//            ex.printStackTrace();
//        }
    }
    private Object keyLock = new Object();
    @Override
    public void keyPressed(KeyEvent e) {
        //To change body of generated methods, choose Tools | Templates.
//        System.out.println("the key is :"+e.getExtendedKeyCode());
        
//            System.out.println("the keypressed is :"+e.getExtendedKeyCode());
            CommandRequest commandRequest = new CommandRequest();
            commandRequest.setProtocol((byte)6);
            commandRequest.setCommand(Command.KEY_PRESSED);
            commandRequest.setHash(hash);
            commandRequest.setRequestorHash(App.hash);
            int[] params = {e.getExtendedKeyCode()};
            
            commandRequest.setParams(params);
            
            this.queueCommandRequest.add(commandRequest);
//            try {
//
//                App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//
//            } catch (IOException ex) {
//                logger.warn("ERROR :"+ex.getMessage());
//                ex.printStackTrace();
//            }
//            try {
//                java.lang.Thread.sleep(500);
//            } catch (InterruptedException ex) {
//                java.util.logging.Logger.getLogger(RemoteViewer.class.getName()).log(Level.SEVERE, null, ex);
//            }
        
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //To change body of generated methods, choose Tools | Templates.
//        System.out.println("the key is :"+e.getExtendedKeyCode());
        
//            System.out.println("the keyrelease is :"+e.getExtendedKeyCode());
            CommandRequest commandRequest = new CommandRequest();
            commandRequest.setProtocol((byte)6);
            commandRequest.setCommand(Command.KEY_RELEASED);
            commandRequest.setHash(hash);
            commandRequest.setRequestorHash(App.hash);
            int[] params = {e.getExtendedKeyCode()};
            
            commandRequest.setParams(params);
            this.queueCommandRequest.add(commandRequest);
//            try {
////                App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
//                
//            } catch (IOException ex) {
//                logger.warn("ERROR :"+ex.getMessage());
//                ex.printStackTrace();
//            }
        
    }
    
    @Override
    public void windowOpened(WindowEvent we) {
        // To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosing(WindowEvent we) {
        // To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowClosed(WindowEvent we) {
        // To change body of generated methods, choose Tools | Templates.
//        this.sessionSendMonitor.stopSenderMonitor();
        App.imageClient.setOldBufferImage(null);
        App.mapRemoteViewer.remove(hash);
    }

    @Override
    public void windowIconified(WindowEvent we) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowActivated(WindowEvent we) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private class CommandSender extends com.mayforever.thread.BaseThread{

        CommandSender() {
            this.startThread();
        }

        @Override
        public void run() {
            while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING){
                CommandRequest commandRequest = null;
                try {
                    commandRequest = queueCommandRequest.get();
                    if (commandRequest!=null){
                        try {
                            
                            App.commandClient.getTcpClient().sendPacket(commandRequest.toBytes());
                        } catch (IOException ex) {
                            java.util.logging.Logger.getLogger(RemoteViewer.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        byte command = commandRequest.getCommand();
                            if (command == Command.KEY_PRESSED){
                                logger.info("kpresed "+commandRequest.getParams()[0] );
                            }else if (command == Command.KEY_RELEASED){
                                logger.info("kpreleased "+commandRequest.getParams()[0] );
                            }
                    }
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(RemoteViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
//    private class SessionSendMonitor extends BaseThread{
//        SessionSendMonitor(){
//            this.startThread();
//        }
//        int sessionLimit = 5;
//        public void stopSenderMonitor()
//        {
//            this.stopThread();
//        }        
//        @Override
//        public void run() {
//            while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING){
//                try {
//                    java.lang.Thread.sleep(2000);
//                    Date date = new Date();
//                    long currentTime = date.getTime();
//                    long timeDiff = currentTime - lastImageDataTimeProcess;
//                    
//                    int diffsec = (int) (timeDiff / (1000));
//                    if(diffsec >= 2){
//                        sessionLimit--;
//                        updateLastSessionDate();
//                        logger.info("Request Timeout Create Another Image Request");
//                        
//                        ImageRequest imageRequest = new ImageRequest();
//        
//                        imageRequest.setHash(hash);
//                        imageRequest.setRequestorHash(App.hash);
//                        App.imageClient.sendImagePacket(imageRequest.toBytes());
//                        
//                        if(sessionLimit == 0){
//                            App.imageClient.socketError(new Exception("Broken Peer Error"));
//                            logger.warn("Peer is Broken");
//                            sessionLimit = 5;
//                        }
//                    }
//                } catch (InterruptedException ex) {
//                    logger.warn("ERROR :"+ex.getMessage());
//                    ex.printStackTrace();
//                }
//            }
//        }
//    }
    
    private void updateLastSessionDate(){
        Date date = new Date();
        lastImageDataTimeProcess = date.getTime();
    }
    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
