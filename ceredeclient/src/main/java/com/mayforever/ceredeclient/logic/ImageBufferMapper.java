/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mayforever.ceredeclient.logic;

import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 *
 * @author mis
 */
public class ImageBufferMapper {
    private int width = 0;
    private int height = 0;
    public ImageBufferMapper(int width, int height){
        this.width = width;
        this.height = height;
        oldScreen = new byte[width][height];
        newScreen = new byte[width][height];
    }
    
    private byte[][] oldScreen = null;
    private byte[][] newScreen = null;
    
    private HashMap<Integer, HashMap<Integer, Byte>> getSendData(BufferedImage bufferImage){
        
        return null;
    }
    
    private BufferedImage getNewScreenBufferedImage(byte[] changes){
        
        return null;
    }
}
