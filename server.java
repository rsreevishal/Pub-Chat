import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class server implements interf {
    @Override
    public ArrayList get()throws RemoteException{
        return messages;
    }
    @Override
    public void send(String mess)throws RemoteException{
        messages.add(mess);
    }
    @Override
    public void sendM(String mem)throws RemoteException{
        members.add(mem);
    }
    @Override
    public ArrayList getM()throws RemoteException{
        return members;
    }
    @Override
    public void removeM(String mem)throws RemoteException{
        for(int i=0;i<members.size();i++){
            if(members.get(i).equals(mem)){
                members.remove(i);
            }
        }
    }
    ArrayList<String> messages;
    ArrayList<String> members;
    server()throws Exception{
        messages = new ArrayList();
        members = new ArrayList();
        messages.add("admin- Welcome to PUBCHAT..!.Enter your message below and click send button;");
    }
    public static void main(String args[])throws Exception{
        Registry reg = LocateRegistry.getRegistry();
        server obj = new server();
        interf Robj = (interf)UnicastRemoteObject.exportObject(obj,0);
        reg.rebind("chatserver",Robj);
    }
}
