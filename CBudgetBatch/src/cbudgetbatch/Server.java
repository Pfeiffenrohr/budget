package cbudgetbatch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.Hashtable;
 
public class Server {
	static String user;
	static String pass;
	static String datenbank;
    private ServerSocket server;
    private int port = 7777;
   
 
    public Server() {
        try {
            server = new ServerSocket(port);
            System.out.println("Server budget erstellt");
        } catch (IOException e) {
            e.printStackTrace();
        }
   }
 
    public static void main(String[] args) {
    	
    	if (args.length != 3)
    	{
    		System.out.println("usage: budget_server <user> <password> <datenbank>");
    		System.exit(1);
    	}
		user = args[0];
		pass = args[1];
		datenbank = args [2];
        Server example = new Server();
       example.handleConnection();
    }

    public void handleConnection() {
       // System.out.println("Waiting for client message...");
 
        //
        // The server do a loop here to accept all connection initiated by the
        // client application.
        //
        while (true) {
            try {
                Socket socket = server.accept();
                new ConnectionHandler(socket,user,pass,datenbank);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


class ConnectionHandler implements Runnable {
	   private Socket socket;
	    String user;
		 String pass;
		 String datenbank;
		 boolean debug=true;
	    public ConnectionHandler(Socket socket,String user,String pass,String datenbank) {
	        this.socket = socket;
	        this.user=user;
	        this.pass=pass;
	        this.datenbank=datenbank;
	        Thread t = new Thread(this);
	        t.start();
	    }
	 
	    public void run() {
	        try
	        {
	            //
	            // Read a message sent by client application
	            //
	            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	            String message = (String) ois.readObject();
	           // System.out.println("Server: Message Received: " + message);
	
	            //
	            // Send a response information to the client application
	            //
	            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	            DBBatch db = new DBBatch();
	            db.dataBaseConnect(user, pass, datenbank);
	            Vector kat = db.getAllActiveKategorien();
	            oos.writeObject("send Kat");
	            if (debug)
	            {
	            	System.out.println("Send Kat");
	            }
	            for (int i=0;i<kat.size();i++)
	            {
	            	oos.writeObject(((Hashtable)kat.elementAt(i)).get("name"));
	            }
	            oos.writeObject("end Kat");
	            if (debug)
	            {
	            	System.out.println("end Kat");
	            }
	            message = (String) ois.readObject();
	            System.out.println("Message = "+message);
	            Vector dat=new Vector();
	            while (! message.equals("ENDE"))
	            {
	            	dat.addElement(message);
	            	message = (String) ois.readObject();
	            	System.out.println("Message = "+message);
	            }
	            //DEBUG
	            while (! message.equals("DEBUG DONE"))
	            {
	            	message = (String) ois.readObject();
	            	System.out.println("Message = "+message);
	            }
	            ///////////////
	            InsertFileInBatch fb = new InsertFileInBatch();
	            oos.writeObject("Done");
	            fb.parse(db, dat);
	            ois.close();
	            oos.close();
	            socket.close();
	 
	           // System.out.println("Waiting for client message...");
	       } catch (IOException e) {
	            e.printStackTrace();
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	    }
	}