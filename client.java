import java.awt.*;
import java.awt.event.*;;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;
import javax.swing.*;
public class client extends JApplet{
    class Sticker extends JLabel{
        int ind ;
        Sticker(int i,ImageIcon img){
            super(img);
            this.ind = i;
            addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent me){
                    try{
                    Robj.send(Name+"- "+"Sent a sticker\n");
                    Robj.send("e#"+ind);}catch(Exception e){}
                }
            });
        }
    }
    JFrame Main;
    JPanel LoginPanel,MainPanel,MessagePanel,ChatPanel;
    JTextArea OnlineM;
    JPanel DisplayArea;
    JTextField MessageArea,UserNameArea;
    JButton Send,Login;
    CardLayout clay = new CardLayout();
    JLabel title1,title2;
    JScrollPane scroll;
    String ep = "emojii\\";
    ImageIcon[] imojii = {new ImageIcon(ep+"e1.png"),new ImageIcon(ep+"e2.png"),new ImageIcon(ep+"e3.png"),new ImageIcon(ep+"e4.png"),new ImageIcon(ep+"e5.png"),new ImageIcon(ep+"e6.png"),new ImageIcon(ep+"e7.png")};
    JMenuBar eitem = new JMenuBar();
    String Name;
    Registry reg;
    interf Robj;
    Timer timer;
    TimerTask task;
    ArrayList<String> blockList;
    client()throws Exception{
        blockList = new ArrayList();
        Main = new JFrame();
        Main.setLayout(new BorderLayout());
        title1 = new JLabel("                       PUBCHAT");
        title2 = new JLabel("                       PUBCHAT");
        title1.setFont(new Font(Font.SERIF,Font.BOLD,35));
        title2.setFont(new Font(Font.SERIF,Font.BOLD,35));
        LoginPanel = new JPanel();
        LoginPanel.setLayout(new GridLayout(10,1));
        MainPanel = new JPanel();
        MainPanel.setLayout(clay);
        ChatPanel = new JPanel();
        ChatPanel.setLayout(new BorderLayout());
        OnlineM = new JTextArea();
        OnlineM.setEditable(false);
        MessagePanel = new JPanel();
        MessagePanel.setLayout(new GridLayout(1,3));
        DisplayArea = new JPanel();
        DisplayArea.setLayout(new GridLayout(100,1));
       // DisplayArea.setEditable(false);
        for(int i=0;i<imojii.length;i++){
            eitem.add(new Sticker(i,imojii[i]));
        }
        scroll = new JScrollPane(DisplayArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        MessageArea = new JTextField();
        UserNameArea = new JTextField();
        Send = new JButton("SEND");
        Login = new JButton("LOGIN");
        reg = LocateRegistry.getRegistry();
        Robj = (interf)reg.lookup("chatserver");
        timer = new Timer();
        task = new TimerTask(){
            @Override
            public void run(){
                try{
                getMessages();
                getMembers();
                }catch(Exception e){}
            }
        };
        Main.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                try{
                    Robj.removeM(Name);
                }catch(Exception ex){
                }
            }
        });
        Main.addKeyListener(new KeyFocus());
        Main.addMouseListener(new MouseFocus());
        MainPanel.addKeyListener(new KeyFocus());
        MainPanel.addMouseListener(new MouseFocus());
        DisplayArea.addKeyListener(new KeyFocus());
        DisplayArea.addMouseListener(new MouseFocus());
        MessageArea.addKeyListener(new KeyFocus());
        MessageArea.addMouseListener(new MouseFocus());
    }
    public void getMessages()throws Exception{
        ArrayList<String> messages = new ArrayList();
        messages = Robj.get();
        DisplayArea.removeAll();
        for(String mess:messages){
            String name = "";
            if(mess.indexOf("-") != -1){
                name = mess.substring(0,mess.indexOf('-'));
                if(blockList.contains(name)){
                    continue;
                }
                else{
                    DisplayArea.add(new JLabel(mess+"\n"));
                }
            }
            else if(mess.indexOf("e#") == 0){
                int ind = Integer.parseInt(mess.substring(2));
                DisplayArea.add(new JLabel(imojii[ind]));
            }
      }
    }
    public void getMembers()throws Exception{
        ArrayList<String> members = new ArrayList();
        members = Robj.getM();
        OnlineM.setText("");
        OnlineM.append("Online Members\n");
        for(int i=0;i<members.size();i++){
            OnlineM.append((i+1)+")"+members.get(i)+"\n");
        }
    }
    public void showGUI(){
        MessageArea.requestFocus();
        MessagePanel.add(MessageArea);
        MessagePanel.add(eitem);
        MessagePanel.add(Send);
        ChatPanel.add(title2,BorderLayout.NORTH);
        ChatPanel.add(scroll,BorderLayout.CENTER);
        ChatPanel.add(MessagePanel,BorderLayout.SOUTH);
        ChatPanel.add(OnlineM,BorderLayout.EAST);
        LoginPanel.add(title1);
        LoginPanel.add(new JLabel("USER NAME:"));
        LoginPanel.add(UserNameArea);
        LoginPanel.add(Login);
        MainPanel.add("login",LoginPanel);
        MainPanel.add("interf",ChatPanel);
        Main.add(MainPanel,BorderLayout.CENTER);
        clay.show(MainPanel,"login");
        Main.setSize(650,650);
        Main.setVisible(true);
        Main.setResizable(false);
        Login.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                MessageArea.requestFocus();
                try{
                if(!UserNameArea.getText().equals("")){
                    Name = UserNameArea.getText();
                    Robj.sendM(Name);
                    clay.show(MainPanel,"interf");
                    timer.scheduleAtFixedRate(task,100,500);
                }}catch(Exception exc){}
            }
        });
        Send.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae){
                sendMess();
            }
        });
    }
    public void sendMess(){
        MessageArea.requestFocus();
        int temp;
        try{
            if(!MessageArea.getText().equals("")){
                if(MessageArea.getText().indexOf("block") != -1 && MessageArea.getText().indexOf("block")<1 ){
                    String blockName = MessageArea.getText().substring(6);
                    if(!blockName.equals(Name)){
                    blockList.add(blockName);
                    Robj.send(Name+"-"+Name+" blocked "+blockName+"\n");
                    }
                }
           else if(MessageArea.getText().indexOf("unblock") != -1 && MessageArea.getText().indexOf("unblock")<1 ){
                    String blockName = MessageArea.getText().substring(8);
                    if(!blockName.equals(Name)){
                    blockList.remove(blockName);
                    Robj.send(Name+"-"+Name+" unblocked "+blockName+"\n");
                    }
                }
           else if(((temp = Containsemojii()) != -1)){
                   Robj.send(Name+"- "+"Sent a sticker\n");
                   Robj.send("e#"+String.valueOf(temp));
           }
           else{
                  String mess = Name+"- "+MessageArea.getText()+"\n";
                  Robj.send(mess);
           }
           MessageArea.setText("");
       }}catch(Exception exce){}
    }
    public int Containsemojii(){
        String[] emojiis = {":)",":(",":p",":|",":o",">(","<3"};
        for(int i =0 ;i<emojiis.length;i++){
            if(MessageArea.getText().indexOf(emojiis[i]) != -1){
                return i;
            }
        }
        return -1;
    }
    public static void main(String args[])throws Exception{
        client obj = new client();
        obj.showGUI();
    }
    
    class KeyFocus implements  KeyListener{
        @Override
        public void keyTyped(KeyEvent e) {}
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == 10){
             sendMess();
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {}
    }
    class MouseFocus implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            MessageArea.requestFocus();
        }
        @Override
        public void mousePressed(MouseEvent e) {
            MessageArea.requestFocus();
        }
        @Override
        public void mouseReleased(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {
            MessageArea.requestFocus();
        }
        @Override
        public void mouseExited(MouseEvent e) {}    
    }
}
