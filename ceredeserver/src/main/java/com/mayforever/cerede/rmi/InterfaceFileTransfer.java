package com.mayforever.cerede.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceFileTransfer extends Remote {
	public void uploadFileToServer(byte[] mybyte, String serverpath, int length) throws RemoteException;
	public byte[] downloadFileFromServer(String servername, String clientPath) throws RemoteException;
//	public String[] listFiles(String serverpath) throws RemoteException;
//	public boolean createDirectory(String serverpath) throws RemoteException;
//	public boolean removeDirectoryOrFile(String serverpath) throws RemoteException;
}
