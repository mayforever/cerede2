/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.device;

import com.mayforever.cerede.protocol.ClipboardInvoke;
import com.mayforever.ceredeclient.App;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import com.mayforever.thread.state.ServiceState;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author mis
 */
public class Clipboard extends BaseThread
        implements FlavorListener, ClipboardOwner
     {
    private java.awt.datatransfer.Clipboard clipboard_ = null;
    private String key_ = null;
    private String requestorHash_ = null;
    private String hash_ = null;
    private Logger logger= null;
//    private Queue<String> clipboardText = null;
    public Clipboard(String requestorHash, String hash, java.awt.datatransfer.Clipboard clipboard){
        this.clipboard_ = clipboard;
        this.key_= App.toHash(requestorHash+hash);
        logger = org.apache.log4j.Logger.getLogger("CLIPBOARD");
//        clipboardText = new Queue<String>();
        this.hash_ = hash;
        this.requestorHash_ = requestorHash;
        this.startThread();
    }
    
    @Override
    public void flavorsChanged(FlavorEvent fe) {
       
        try {
           // String clipboardText = (String) this.clipboard_.getData(DataFlavor.stringFlavor);
           String clipboardText = (String) this.clipboard_.getContents(null).getTransferData(DataFlavor.stringFlavor); 
           this.logger.info("ClipBoard UPDATED:" + clipboardText);
            App.rmiClient.addClipboard(key_, clipboardText);
        } catch (RemoteException ex) {
//            Logger.getLogger(Clipboard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedFlavorException ex) {
            logger.warn(ex);
//            ex.printStackTrace();
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
//            ex.printStackTrace();
        }
        ClipboardInvoke clipboardInvoke= new ClipboardInvoke();
        clipboardInvoke.setProtocol((byte) 7);
        clipboardInvoke.setCommand((byte)0);
        clipboardInvoke.setHash(hash_);
        clipboardInvoke.setRequestorHash(requestorHash_);


        App.commandClient.sendClipboardInvoke(clipboardInvoke);
    }
    
//    public void updateClipboard(String clipboardText){
//        StringSelection stringSelection = new StringSelection(clipboardText);
//        this.clipboard_.setContents(stringSelection, this);
//    }

    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard clpbrd, Transferable t) {
        logger.warn("Lost ownership");
    }

    @Override
    public void run() {
        this.clipboard_.addFlavorListener(this);
        while(this.getServiceState() == ServiceState.RUNNING){
            try {
                java.lang.Thread.sleep(100);
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
}
