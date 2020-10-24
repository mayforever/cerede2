package com.mayforever.cerede.rmi;

import com.mayforever.cerede.data.TCPData;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface InterfaceImage extends Remote{
    public HashMap<Short, HashMap<Short, Integer>> getChangeColumns(String key)
                    throws RemoteException;
    public void addChangeColumns(String key, HashMap<Short, HashMap<Short, Integer>> value)
                    throws RemoteException;
    public HashMap<String, TCPData> getRemoteList() throws RemoteException;
    public String getClipboard(String key) throws RemoteException;
    public void addClipboard(String key, String value) throws RemoteException;
}
