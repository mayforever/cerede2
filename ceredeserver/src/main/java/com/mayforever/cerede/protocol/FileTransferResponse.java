package com.mayforever.cerede.protocol;

import java.nio.ByteOrder;

import com.mayforever.tools.BitConverter;

public class FileTransferResponse extends BaseClass{

	public int getSizeOfFile() {
		return sizeOfFile;
	}

	public void setSizeOfFile(int sizeOfFile) {
		this.sizeOfFile = sizeOfFile;
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
	private int sizeOfFile = 0;
	private String pathDestination = "";
	private int sizeOfPathDestination = 0;
//	private int result = 0;
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		this.setSizeOfPathDestination(pathDestination.length());
		this.setRequestorHashSize(requestorHash.length());
		this.setTotalSize(1+4+4+sizeOfPathDestination+4+requestorHashSize+4);
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
		System.arraycopy(BitConverter.intToBytes(sizeOfPathDestination, ByteOrder.BIG_ENDIAN),
                0, data, index, 4);
		index+=4;
		System.arraycopy(pathDestination.getBytes(), 0, data, index, sizeOfPathDestination);
		index+=sizeOfPathDestination;
		System.arraycopy(BitConverter.intToBytes(sizeOfFile, ByteOrder.BIG_ENDIAN),
                0, data, index, 4);
		index+=4;
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
		this.sizeOfPathDestination = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
        index+=4;
        this.setPathDestination(new java.lang.String(data, index, sizeOfPathDestination));
        index+=sizeOfPathDestination;
        this.setSizeOfFile(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
        index+=4;
	}

}
