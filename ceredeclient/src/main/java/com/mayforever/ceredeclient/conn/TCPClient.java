package com.mayforever.ceredeclient.conn;


import com.mayforever.queue.Queue;
import java.nio.channels.AsynchronousSocketChannel;


import org.apache.log4j.Logger;



public class TCPClient 
		implements com.mayforever.network.newtcp.ClientListener{
	private com.mayforever.network.newtcp.TCPClient tcpClient = null;
	private byte[] tempData = null;
	public com.mayforever.network.newtcp.TCPClient getTcpClient() {
		return tcpClient;
	}

	AsynchronousSocketChannel asc = null;
//	private Queue<byte[]> dataRecieveQueue = null;
//	private Logger logger = Logger.getLogger("TCPClient");
	private String hash = null;
	
	public TCPClient(AsynchronousSocketChannel asc) {
//		this.dataRecieveQueue = new Queue<>();
		this.asc = asc;
		this.tcpClient = new com.mayforever.network.newtcp.TCPClient(asc);
		this.tcpClient.addListener(this); 
//		this.tcpClient.setAllocation(2048 * 5);
	}
	public void packetData(byte[] data) {
		// TODO Auto-generated method stub
//		this.dataRecieveQueue.add(data);
	}

	public void socketError(Exception e) {
		// TODO Auto-generated method stub

	}
	
	
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
