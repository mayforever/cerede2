package com.mayforever.cerede.rmi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BaseClassFileTransfer extends UnicastRemoteObject implements InterfaceFileTransfer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected BaseClassFileTransfer() throws RemoteException {
//		File storageDir = new File (s);
//		storageDir.mkdir();
	}
	
	public void uploadFileToServer(byte[] mydata, String key, int length) throws RemoteException {
			
    	try {
    		File serverpathfile = new File("");
    		FileOutputStream out=new FileOutputStream(serverpathfile);
    		byte [] data=mydata;
			
    		out.write(data);
			out.flush();
	    	out.close();
	 
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    	
    	System.out.println("Done writing data...");
		
	}
	
	public byte[] downloadFileFromServer(String key,String clientPath) throws RemoteException {
					
		byte [] mydata;	
		
			File serverpathfile = new File("");			
			mydata=new byte[(int) serverpathfile.length()];
			FileInputStream in;
			try {
				in = new FileInputStream(serverpathfile);
				try {
					in.read(mydata, 0, mydata.length);
				} catch (IOException e) {
					
					e.printStackTrace();
				}						
				try {
					in.close();
				} catch (IOException e) {
				
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				
				e.printStackTrace();
			}		
			
			return mydata;
				 
	}

	
//	public String[] listFiles(String serverpath) throws RemoteException {
//		File serverpathdir = new File(serverpath);
//		return serverpathdir.list();
//		
//	}
	
//	public boolean createDirectory(String serverpath) throws RemoteException {	
//		File serverpathdir = new File(serverpath);
//		return serverpathdir.mkdir();
//		
//	}
//
//	public boolean removeDirectoryOrFile(String serverpath) throws RemoteException {
//		File serverpathdir = new File(serverpath);
//		return serverpathdir.delete();
//		
//	}


}
