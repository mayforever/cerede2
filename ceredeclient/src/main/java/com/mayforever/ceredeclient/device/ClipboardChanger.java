/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.device;

import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 *
 * @author mis
 */
public class ClipboardChanger implements ClipboardOwner{
    private java.awt.datatransfer.Clipboard clipboard_ = null;
    public ClipboardChanger(java.awt.datatransfer.Clipboard clipboard){
        this.clipboard_ = clipboard;
    }
    public void updateClipboard(String clipboardText){
        StringSelection stringSelection = new StringSelection(clipboardText);
        this.clipboard_.setContents(stringSelection, this);
    }

    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard clpbrd, Transferable t) {
        
    }
}
