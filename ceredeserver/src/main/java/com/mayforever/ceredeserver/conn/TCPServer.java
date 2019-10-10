package com.mayforever.ceredeserver.conn;

import java.nio.channels.AsynchronousSocketChannel;


public class TCPServer implements com.mayforever.network.newtcp.ServerListener{
	public com.mayforever.network.newtcp.TCPServer tcpServer = null;
	
	public TCPServer(int port,String ip){
		this.tcpServer = new com.mayforever.network.newtcp.TCPServer(port,ip);
		this.tcpServer.addListener(this);
	}
	
	public void acceptSocket(AsynchronousSocketChannel arg0) {
		// TODO Auto-generated method stub

		new TCPClient(arg0);
	}

	public void socketError(Exception arg0) {
		// TODO Auto-generated method stub
		
	}
	


}
