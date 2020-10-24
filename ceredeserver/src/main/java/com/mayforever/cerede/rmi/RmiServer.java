package com.mayforever.cerede.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;


public class RmiServer{
	
//	protected RmiImageServer(int port) throws RemoteException {
//		super(port);
//		mapChangesColumn = new HashMap<>();
//		BaseClassImage baseClassImage = new BaseClassImage(this);
//		try {
//			Naming.bind("rmi://0.0.0.0:"+port+"/BaseClassImage", baseClassImage);
//			
//		} catch (MalformedURLException | AlreadyBoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// TODO Auto-generated constructor stub
//	}
	
	public RmiServer(int port, String ip) {
		mapChangesColumn = new HashMap<>();
		mapClipboardText = new HashMap<String, String>();
		mapTransferFile = new HashMap<String, byte[]>();
		BaseClassImage baseClassImage;
		BaseClassFileTransfer baseClassFileTransfer;
		try {
			baseClassImage = new BaseClassImage();
			baseClassFileTransfer = new BaseClassFileTransfer();
    		
			Registry r = LocateRegistry.createRegistry(port);
//			r = LocateRegistry.getRegistry();
			r.bind("cerede", baseClassImage);
			r.bind("filetransfer", baseClassFileTransfer);
		} catch (RemoteException | AlreadyBoundException  e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
		
	}

	public HashMap<String, HashMap<Short, HashMap<Short, Integer>>> mapChangesColumn = null;
	public HashMap<String, String> mapClipboardText = null;
	public HashMap<String, byte[]> mapTransferFile = null;

}
