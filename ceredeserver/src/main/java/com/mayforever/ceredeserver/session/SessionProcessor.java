package com.mayforever.ceredeserver.session;

import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.mayforever.cerede.protocol.ClipboardInvoke;
import com.mayforever.cerede.protocol.CommandRequest;
import com.mayforever.cerede.protocol.FileTransferRequest;
import com.mayforever.cerede.protocol.FileTransferResponse;
import com.mayforever.cerede.protocol.ImageRequest;
import com.mayforever.cerede.protocol.ImageResponse;
import com.mayforever.ceredeserver.Launcher;

public class SessionProcessor extends com.mayforever.thread.BaseThread{

	private Logger logger = null;
	
	public SessionProcessor() {
		// TODO Auto-generated constructor stub
		logger = Logger.getLogger("SessionProcessor");
	}
	
	
	public void startSession(){	
		this.startThread();
	}
	
	public void stopSession(){
		this.stopThread();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(this.getServiceState() == com.mayforever.thread.state.ServiceState.RUNNING) {
			byte[] data;
			try {
				data = Launcher.dataProcess.get();
				if (data != null) {
					logger.debug(""+data[0]);
					if(data[0] == 2) {
						ImageRequest imageRequest = new ImageRequest();
						imageRequest.fromBytes(data);
						if(Launcher.controllerMap.containsKey(imageRequest.gethash())) {
							this.logger.debug("hash to : " + imageRequest.gethash());
							this.logger.debug("hash to user : " +  Launcher.controllerMap.get(imageRequest.gethash()).getUser());
							this.logger.debug("hash From : " + imageRequest.getRequestorHash());
							this.logger.debug("hash From user : " + Launcher.controllerMap.get(imageRequest.getRequestorHash()).getUser());
							try {
								Launcher.controllerMap.get(imageRequest.gethash()).getTcpImageClient().getTcpClient().sendPacket(data);
								logger.debug("Image Request send");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}catch(NullPointerException e) {
								
							}
						}else {
							ImageResponse imageResponse = new ImageResponse();
							imageResponse.setHash(imageRequest.gethash());
							imageResponse.setRequestorHash(imageRequest.getRequestorHash());
							imageResponse.setWidth(0);
							imageResponse.setHeight(0);
							
							imageResponse.setResult(9990001);
							imageResponse.setResultString("User has been offline");
							logger.debug("Image Response Send");
							try {
								Launcher.controllerMap.get(imageResponse.getRequestorHash()).getTcpImageClient()
											.getTcpClient().sendPacket(imageResponse.toBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
					else if(data[0] == 3){
						logger.debug("image response to process");
						ImageResponse imageResponse = new ImageResponse();
						imageResponse.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(imageResponse.getRequestorHash())) {
							try {
								Launcher.controllerMap.get(imageResponse.getRequestorHash()).getTcpImageClient()
									.getTcpClient().sendPacket(imageResponse.toBytes());
								logger.debug("image response to processed");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}else {
							imageResponse = new ImageResponse();
							imageResponse.setHash(imageResponse.getHash());
							imageResponse.setRequestorHash(imageResponse.getRequestorHash());
							imageResponse.setWidth(0);
							imageResponse.setHeight(0);
							imageResponse.setResult((byte)2);
							
							logger.debug("Message Response Send");
							try {
								Launcher.controllerMap.get(imageResponse.getHash()).getTcpImageClient()
											.getTcpClient().sendPacket(imageResponse.toBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else if(data[0] == 4){
						logger.debug("file request to process");
						FileTransferRequest fileTransferRequest = new FileTransferRequest();
						fileTransferRequest.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(fileTransferRequest.getHash())) {
							try {
								
									Launcher.controllerMap.get(fileTransferRequest.getHash()).getTcpCommandListener()
										.getTcpClient().sendPacket(data);
								
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}catch(NullPointerException npe) {
								
							}
						}else {
							
						}
					}
					else if(data[0] == 5){
						logger.debug("file response to process");
						FileTransferResponse fileTransferResponse = new FileTransferResponse();
						fileTransferResponse.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(fileTransferResponse.getRequestorHash())) {
							try {
								
									Launcher.controllerMap.get(fileTransferResponse.getRequestorHash()).getTcpCommandListener()
										.getTcpClient().sendPacket(data);
								
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}catch(NullPointerException npe) {
								
							}
						}else {
							
						}
					}
					else if(data[0] ==6){
						CommandRequest commandRequest = new CommandRequest();
						commandRequest.fromBytes(data);
//						System.out.println(Arrays.toString(data));
//						System.out.println(Arrays.toString(commandRequest.toBytes()));
						if(Launcher.controllerMap.containsKey(commandRequest.getHash())) {
							try {
								
									Launcher.controllerMap.get(commandRequest.getHash()).getTcpCommandListener()
										.getTcpClient().sendPacket(data);
								
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}catch(NullPointerException npe) {
								
							}
						}else {
							
						}
					}
					else if(data[0] == 7) {
						System.out.println(Arrays.toString(data));
						ClipboardInvoke clipboardInvoke = new ClipboardInvoke();	
						clipboardInvoke.fromBytes(data);
						
						if(Launcher.controllerMap.containsKey(clipboardInvoke.getHash())) {
							try {
								
									Launcher.controllerMap.get(clipboardInvoke.getHash()).getTcpCommandListener()
										.getTcpClient().sendPacket(data);
								
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}catch(NullPointerException npe) {
								
							}
						}else {
							
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
