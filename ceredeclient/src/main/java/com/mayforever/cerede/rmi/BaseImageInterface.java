package com.mayforever.cerede.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface BaseImageInterface extends Remote{
	public HashMap<Short, HashMap<Short, Integer>> getChangeColumns(String key)
						throws RemoteException;
	public void addChangeColumns(String key, HashMap<Short, HashMap<Short, Integer>> value)
			throws RemoteException;
}
