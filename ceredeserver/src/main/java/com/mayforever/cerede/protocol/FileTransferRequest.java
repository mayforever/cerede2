package com.mayforever.cerede.protocol;

import java.nio.ByteOrder;

import com.mayforever.tools.BitConverter;

public class FileTransferRequest extends BaseClass{

	public String getPathSource() {
		return pathSource;
	}

	public void setPathSource(String pathSource) {
		this.pathSource = pathSource;
	}

	public int getSizeOfPathSource() {
		return sizeOfPathSource;
	}

	public void setSizeOfPathSource(int sizeOfPathSource) {
		this.sizeOfPathSource = sizeOfPathSource;
	}

	public String getPathDestination() {
		return pathDestination;
	}

	public void setPathDestination(String pathDestination) {
		this.pathDestination = pathDestination;
	}

	public int getSizeOfPathDestination() {
		return sizeOfPathDestination;
	}

	public void setSizeOfPathDestination(int sizeOfPathDestination) {
		this.sizeOfPathDestination = sizeOfPathDestination;
	}

//	private int sizeOfFile = 0;
	private String pathSource = "";
	private int sizeOfPathSource = 0;
	private String pathDestination = "";
	private int sizeOfPathDestination = 0;
	public int getRequestorHashSize() {
		return requestorHashSize;
	}

	public void setRequestorHashSize(int requestorHashSize) {
		this.requestorHashSize = requestorHashSize;
	}

	public String getRequestorHash() {
		return requestorHash;
	}

	public void setRequestorHash(String requestorHash) {
		this.requestorHash = requestorHash;
	}

	private int requestorHashSize = 0;
	private String requestorHash = "";
//	private int result = 0;
	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		this.setTotalSize(1+4+sizeOfPathSource+4+sizeOfPathDestination+4+
				requestorHashSize + 4 + hashSize + 4);
		byte[] data = new byte[this.getTotalSize()];
		int index = 0;
		data[index] = (byte)0;
		index++;
		System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
				0, data, index, 4);
		index+=4;
		
		System.arraycopy(BitConverter.intToBytes(requestorHashSize, ByteOrder.BIG_ENDIAN),
                0, data, index, 4);
		index+=4;
		System.arraycopy(requestorHash.getBytes(), 0, data, index, requestorHashSize);
		index+=requestorHashSize;
        System.arraycopy(BitConverter.intToBytes(hashSize, ByteOrder.BIG_ENDIAN),
                0, data, index, 4);
		index+=4;
		System.arraycopy(hash.getBytes(), 0, data, index, hash.length());
		index+=hash.length();
		System.arraycopy(BitConverter.intToBytes(getSizeOfPathSource(), ByteOrder.BIG_ENDIAN),
                0, data, index, 4);
		index+=4;
		System.arraycopy(pathSource.getBytes(), 0, data, index, getSizeOfPathSource());
		index+=getSizeOfPathSource();
		System.arraycopy(BitConverter.intToBytes(sizeOfPathDestination, ByteOrder.BIG_ENDIAN),
                0, data, index, 4);
		index+=4;
		System.arraycopy(pathDestination.getBytes(), 0, data, index, sizeOfPathDestination);
		index+=sizeOfPathDestination;
//		System.arraycopy(BitConverter.intToBytes(sizeOfFile, ByteOrder.BIG_ENDIAN),
//                0, data, index, 4);
//		index+=4;
		return data;
	}

	@Override
	public void fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		int index = 0;
		this.setProtocol(data[index]);
		index++;
		this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		this.requestorHashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setRequestorHash(new java.lang.String(data, index, requestorHashSize));
        index+=requestorHashSize;
        this.hashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setHash(new java.lang.String(data, index, hashSize));
        index+=hashSize;
        this.sizeOfPathSource = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setPathSource(new java.lang.String(data, index, sizeOfPathSource));
        index+=sizeOfPathSource;
        this.sizeOfPathDestination = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setPathDestination(new java.lang.String(data, index, sizeOfPathDestination));
        index+=sizeOfPathDestination;
        
	}	
	public int getHashSize() {
		return hashSize;
	}

	public void setHashSize(int hashSize) {
		this.hashSize = hashSize;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	private int hashSize = 0;
	private String hash = "";
	
}
