package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
//import java.util.concurrent.BlockingQueue;

import Messages.Packet;

public class Client implements Runnable {
    // user info
    /*private int id;*/
    private String name;
    private List<String> channels;

    // client thread
    private Thread clientThread;

    // connection info
    private boolean isConnected;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client(/*int id,*/ Socket socket/*, BlockingQueue<Serializable> requests, List<Client> connectedClients*/) {
        /*this.id = id;*/
        this.name = "guest";
        channels = new ArrayList<>();
        isConnected = true;
        this.socket = socket;

        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        clientThread = new Thread(this);
        clientThread.start();
    }

    public void setName(String name) {this.name = name;}
    public void addSubscription(String channel) {channels.add(channel);}
    public void removeSubscription(String channel) {channels.remove(channel);}
    public boolean isSubscribed(String channel) {return channels.contains(channel);}
    /*public int getID() {return id;}*/
    public Thread getClientThread() {return clientThread;}
    public void terminateConnection() {isConnected = false;}

    public void sendPacket(Packet p) {
        try {
            // send packet to client
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(isConnected) {
                // serve client until client disconnects
            }
        }
        catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            // remove this object when it disconnects
        }
    }
}
