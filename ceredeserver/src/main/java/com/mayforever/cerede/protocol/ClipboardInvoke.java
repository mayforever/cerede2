package com.mayforever.cerede.protocol;

import java.nio.ByteOrder;

import com.mayforever.tools.BitConverter;

public class ClipboardInvoke extends BaseClass{

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
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

    public int getHashSize() {
        return hashSize;
    }

    public void setHashSize(int hashSize) {
        this.hashSize = hashSize;
    }
        
    
        private String hash = "";
	private String requestorHash = "";
        
	private int requestorHashSize = 0;
	private int hashSize = 0;
        
	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}


        
	private byte command = 0; 
	public byte[] toBytes() {
		// TODO Auto-generated method stub
                this.setHashSize(hash.length());
                this.setRequestorHashSize(requestorHash.length());
                this.setTotalSize(2 + 4 + this.getHashSize()
                            + this.getRequestorHashSize() + 4+ 4);
		byte[] data = new byte[this.getTotalSize()];
		int index = 0;
		data[index] = this.getProtocol();
		index++;
                System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
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
		data[index] = this.getCommand();
		index++;
		
		return data;
	}

	public void fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		int index = 0;
		this.setProtocol(data[index]);
		index++;
                this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		this.hashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
		index+=4;
		this.setHash(new java.lang.String(data, index, hashSize));
		index+=hashSize;
		this.requestorHashSize = BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN);
		index+=4;
		this.setRequestorHash(new java.lang.String(data, index, requestorHashSize));
		index+=requestorHashSize;
		this.setCommand(data[index]);
		index++;
		
	}

}
