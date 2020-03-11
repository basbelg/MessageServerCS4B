package ServerPackage;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class Server implements Runnable {
    public static BlockingQueue<Serializable> requests;

    private int port;
    private ServerSocket serverSocket;

    private ArrayList<Client> ConnectedClients;
    private HashMap<String, Integer> SubscriberList;

    public Server() {
        port = 8000;
        ConnectedClients = new ArrayList<>();
        SubscriberList = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
