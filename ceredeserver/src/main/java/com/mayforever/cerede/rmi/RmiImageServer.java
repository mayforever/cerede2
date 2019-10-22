package com.mayforever.cerede.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;

public class RmiImageServer{
	
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
	
	public RmiImageServer(int port, String ip) {
		mapChangesColumn = new HashMap<>();
		mapClipboardText = new HashMap<String, String>();
		BaseClassImage baseClassImage;
		
		try {
			baseClassImage = new BaseClassImage();
			
    		
			Registry r = LocateRegistry.createRegistry(port);
//			r = LocateRegistry.getRegistry();
			r.bind("cerede", baseClassImage);
			
		} catch (RemoteException | AlreadyBoundException  e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			
		
	}

	public HashMap<String, HashMap<Short, HashMap<Short, Integer>>> mapChangesColumn = null;
	public HashMap<String, String> mapClipboardText = null;
	/**
	 * 
	 */
//	private static final long serialVersionUID = 1L;

}
