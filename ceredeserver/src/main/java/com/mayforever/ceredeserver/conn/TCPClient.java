package com.mayforever.ceredeserver.conn;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.mayforever.cerede.protocol.AccessRequest;
import com.mayforever.cerede.protocol.AccessResponse;
import com.mayforever.ceredeserver.Launcher;
import com.mayforever.ceredeserver.conn.data.TCPData;
import com.mayforever.queue.Queue;
import com.mayforever.thread.BaseThread;
import com.mayforever.tools.BitConverter;

public class TCPClient 
		extends BaseThread
		implements com.mayforever.network.newtcp.ClientListener{
	private com.mayforever.network.newtcp.TCPClient tcpClient = null;
	private byte[] tempData = null;
	public com.mayforever.network.newtcp.TCPClient getTcpClient() {
		return tcpClient;
	}

	AsynchronousSocketChannel asc = null;
	private Queue<byte[]> dataRecieveQueue = null;
	private Logger logger = Logger.getLogger("TCPClient");
	private String hash = null;
	
	public TCPClient(AsynchronousSocketChannel asc) {
		this.dataRecieveQueue = new Queue<>();
		this.asc = asc;
		this.tcpClient = new com.mayforever.network.newtcp.TCPClient(asc);
		this.tcpClient.addListener(this); 
		
		this.startThread();
//		this.tcpClient.setAllocation(2048 * 5);
	}
	public void packetData(byte[] data) {
		// TODO Auto-generated method stub
		this.dataRecieveQueue.add(data);
	}

	public void socketError(Exception e) {
		// TODO Auto-generated method stub
		if(hash != null) {
			if(Launcher.controllerMap.containsKey(hash)) {
				Launcher.controllerMap.remove(hash);
				logger.warn("the hash "+ hash + " was disconected 1");
				
			}else {
				logger.warn("the hash "+ hash + " was disconected 2");
			}
			
		}
		
		
		e.printStackTrace();
//		this.getTcpClient().disconnect();
		this.stopThread();
	}
	
	public void processData(byte[] data) {
		if(data.length == 0) {
			return;
		}
		if(data[0] == 0) {
			logger.debug("accessRequest receive ");
			
			AccessRequest accessRequest = new AccessRequest();
			accessRequest.fromBytes(data);
			
			String user = accessRequest.getUsername();
			logger.debug("the username was " + user);
			String password = accessRequest.getPassword();
			logger.debug("the password was " + password);
			logger.debug("Control No . "+ accessRequest.getControl());
			
			
			
			
			hash = Launcher.toHash(user+password);
			
	        if(!Launcher.controllerMap.containsKey(hash)) {
	        	Launcher.controllerMap.put(hash, new TCPData(user, password));
	        	logger.debug("not contain hash "+hash);
	        }
			if(accessRequest.getControl() == 0) {
				Launcher.controllerMap.get(hash).setTcpImageClient(this);
				logger.debug("Image Receiver Client has been alive from user "+ accessRequest.getUsername());
			}else if(accessRequest.getControl() == 1){
				Launcher.controllerMap.get(hash).setTcpCommandListener(this);
				logger.debug("Command Receiver Client has been alive from user "+ accessRequest.getUsername());
			}
			
			AccessResponse accessResponse = new AccessResponse();
			accessResponse.setResult(0);
			accessResponse.setResultString("ok");
			accessResponse.setProtocol((byte)1);
			try {
				logger.debug("Control No . "+ accessResponse.toBytes().length);
				this.tcpClient.sendPacket(accessResponse.toBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
 			}
		}else {
			Launcher.dataProcess.add(data);
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
			byte[] data = null;
			try {
				data = this.dataRecieveQueue.get();
				if (data != null) {
					
                  if (tempData == null || tempData.length  == 0) {
                	  tempData = data;
                  }else {
                          byte[] dataPending = tempData;
                          tempData = new byte[tempData.length + data.length];
                          System.arraycopy(dataPending, 0, tempData, 0, dataPending.length);
                          System.arraycopy(data, 0, tempData, dataPending.length, data.length);
                  }
                 
                  logger.debug(tempData.length);
                  int dataProcessSize = BitConverter.bytesToInt(tempData, 1, ByteOrder.BIG_ENDIAN);

                  logger.debug(dataProcessSize+" == "+ tempData.length);
                  logger.debug(" == "+ Arrays.toString(tempData));
		
                  do {
					if(dataProcessSize == tempData.length) {
						this.processData(tempData);
						tempData = new byte[0];
					}else if(dataProcessSize < tempData.length) {
						byte[] dataToProcess = new byte[dataProcessSize];
						System.arraycopy(tempData, 0, dataToProcess, 0, dataProcessSize);
						this.processData(dataToProcess);
						byte[] newtempData = new byte[tempData.length - dataProcessSize];
						System.arraycopy(tempData, dataProcessSize, newtempData, 0,  tempData.length - dataProcessSize);
						this.tempData = newtempData;
						logger.debug("newtempData length : " + newtempData.length);
					}
					logger.debug("tempData length : " + tempData.length);
					if(tempData.length < 5) {
//		                 System.out.println("data break");
		                 break;
		            }else {    
		                dataProcessSize = BitConverter.bytesToInt(tempData, 1, ByteOrder.BIG_ENDIAN);
		                if(dataProcessSize == 0){
		                    break;
		                }
		            }
					logger.debug("dataProcessSize length : " + dataProcessSize );
                  }while(tempData.length > dataProcessSize);
//                  System.out.println(tempData.length);
              }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
