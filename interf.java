import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
public interface interf extends Remote{
    public ArrayList get()throws RemoteException;
    public void send(String mess)throws RemoteException;
    public void sendM(String mem)throws RemoteException;
    public ArrayList getM()throws RemoteException;
    public void removeM(String mem)throws RemoteException;
}
