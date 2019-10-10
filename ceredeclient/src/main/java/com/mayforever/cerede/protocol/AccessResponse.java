package com.mayforever.cerede.protocol;

import java.nio.ByteOrder;

import com.mayforever.tools.BitConverter;

public class AccessResponse extends BaseClass {
	private int result;
	private String resultString;
	private int sizeOfResultString;
	public String getResultString() {
		return resultString;
	}

	public void setResultString(String resultString) {
		this.resultString = resultString;
	}

	@Override
	public byte[] toBytes() {
		// TODO Auto-generated method stub
		this.setTotalSize(resultString.length()+1+4+4+4);
		byte[] data = new byte[this.getTotalSize()];
		int index = 0;
		data[index] = (byte)0;
		index++;
		System.arraycopy(BitConverter.intToBytes(this.getTotalSize(), ByteOrder.BIG_ENDIAN),
				0, data, index, 4);
		index+=4;
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

	@Override
	public void fromBytes(byte[] data) {
		// TODO Auto-generated method stub
		int index = 0;
		this.setProtocol(data[index]);
		index++;
		this.setTotalSize(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		this.setResult(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		this.setSizeOfResultString(BitConverter.bytesToInt(data, index, ByteOrder.BIG_ENDIAN));
		index+=4;
		
		this.setResultString(new java.lang.String(data, index, sizeOfResultString));
		index+=sizeOfResultString;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public int getSizeOfResultString() {
		return sizeOfResultString;
	}

	public void setSizeOfResultString(int sizeOfResultString) {
		this.sizeOfResultString = sizeOfResultString;
	}

}
