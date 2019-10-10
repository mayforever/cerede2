package com.mayforever.cerede.protocol;

import java.nio.ByteOrder;

import com.mayforever.tools.BitConverter;

public class ImageResponse extends BaseClass{

	private String requestorHash = "";
	private int requestorHashSize = 0;

	private int result;
	private String resultString;
	private int sizeOfResultString;
	public int getHashSize() {
		return hashSize;
	}

	public void setHashSize(int hashSize) {
		this.hashSize = hashSize;
	}
	private int hashSize = 0;
	
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
	private int width = 0;
        private int height = 0;


	public byte[] toBytes() {
		// TODO Auto-generated method stub

        this.setRequestorHashSize(requestorHash.length());
        this.setHashSize(hash.length());
        this.setSizeOfResultString(resultString.length());
        this.setTotalSize(1+4+4+4+requestorHashSize+4+4+hashSize+4+4+sizeOfResultString);
        byte[] data = new byte[this.getTotalSize()];
        int index = 0;

        data[index] = (byte)3;
        index++;
        System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(BitConverter.intToBytes(this.getWidth(), ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(BitConverter.intToBytes(this.getHeight(), ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;


        System.arraycopy(BitConverter.intToBytes(hashSize, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(hash.getBytes(), 0, data, index, hash.length());
        index+=hash.length();
        System.arraycopy(BitConverter.intToBytes(requestorHashSize, ByteOrder.BIG_ENDIAN),
                        0, data, index, 4);
        index+=4;
        System.arraycopy(requestorHash.getBytes(), 0, data, index, requestorHash.length());
        index+=requestorHash.length();
       
        System.arraycopy(BitConverter.intToBytes(result, ByteOrder.BIG_ENDIAN),
				0, data, index, 4);
		index+=4;
		System.arraycopy(BitConverter.intToBytes(resultString.length(), ByteOrder.BIG_ENDIAN),
				0, data, index, 4);
		index+=4;
		
		System.arraycopy(resultString.getBytes(), 0, data, index, resultString.length());
		index+=resultString.length();
        return data;
	}

	private String hash = "";
	public void fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		
        int index = 0;
        this.setProtocol(data[index]);
        index++;

        this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
        index+=4;
        this.setWidth(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
        index+=4;
        this.setHeight(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
        index+=4;

        this.hashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setHash(new java.lang.String(data, index, hashSize));
        index+=hashSize;
    
        this.requestorHashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setRequestorHash(new java.lang.String(data, index, requestorHashSize));
        index+=requestorHashSize;
        
        this.setResult(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		this.setSizeOfResultString(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		
		this.setResultString(new java.lang.String(data, index, sizeOfResultString));
		index+=sizeOfResultString;
	}

	public String getRequestorHash() {
		return requestorHash;
	}

	public void setRequestorHash(String requestorHash) {
		this.requestorHash = requestorHash;
	}

	public int getRequestorHashSize() {
		return requestorHashSize;
	}

	public void setRequestorHashSize(int requestorHashSize) {
		this.requestorHashSize = requestorHashSize;
	}



	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}

	public int getSizeOfResultString() {
		return sizeOfResultString;
	}

	public void setSizeOfResultString(int sizeOfResultString) {
		this.sizeOfResultString = sizeOfResultString;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}


	
}
