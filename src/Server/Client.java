package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import Messages.Packet;

public class Client implements Runnable {
    // client info
    private String name;
    private List<String> channels;

    // client thread
    private Thread clientThread;

    // connection info
    private boolean isConnected;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    // server info
    private BlockingQueue<Packet> requests;
    private List<Client> clients;
    private HashMap<String, List<Client>> subscribers;

    public Client(Socket socket, BlockingQueue<Packet> requests, List<Client> clients, HashMap<String, List<Client>> subscribers) {
        try {
            // set client info
            this.name = "guest";
            channels = new ArrayList<>();

            // set connection info
            isConnected = true;
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            // set server info
            this.requests = requests;
            this.clients = clients;
            this.subscribers = subscribers;
            clients.add(this);

            // create and start client-handling thread
            clientThread = new Thread(this);
            clientThread.start();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void setName(String name) {this.name = name;}

    public void addSubscription(String channel) {
        channels.add(channel);
        subscribers.get(channel).add(this);
    }

    public void removeSubscription(String channel) {
        channels.remove(channel);
        subscribers.get(channel).remove(this);
    }

    public final boolean isSubscribed(String channel) {return channels.contains(channel);}

    public Thread getClientThread() {return clientThread;}

    public void terminateConnection() {isConnected = false;}

    public void sendPacket(Packet p) {
        try {
            // send packet to client
            out.writeObject(p);

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
                Packet p = (Packet) in.readObject();
                requests.add(p);
            }
        }
        catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            removeConnection();
        }
    }

    private void removeConnection() {
        clients.remove(this);
        for(String channel: channels) {
            subscribers.get(channel).remove(this);
        }
    }
}
