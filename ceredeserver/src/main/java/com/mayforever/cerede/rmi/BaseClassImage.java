package com.mayforever.cerede.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import com.mayforever.ceredeserver.Launcher;

public class BaseClassImage  extends UnicastRemoteObject implements BaseImageInterface{
	protected BaseClassImage() throws RemoteException {
		super();
		
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private RmiImageServer rmiImageServer = null;
	
	
	
	@Override
	public HashMap<Short, HashMap<Short, Integer>> getChangeColumns(String key) {
		// TODO Auto-generated method stub
		HashMap<Short, HashMap<Short, Integer>> needToChange = Launcher.rmiImageServer.mapChangesColumn.get(key);
		Launcher.rmiImageServer.mapChangesColumn.remove(key);
		return needToChange;
	}

	@Override
	public void addChangeColumns(String key, HashMap<Short, HashMap<Short, Integer>> value) {
		// TODO Auto-generated method stub
		Launcher.rmiImageServer.mapChangesColumn.put(key, value);
	}

}
