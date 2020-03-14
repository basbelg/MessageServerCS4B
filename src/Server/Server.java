package Server;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Server implements Runnable {
    public static BlockingQueue<Serializable> requests;

    private int port;
    private boolean shutdown;
    private Socket socket;
    private ServerSocket serverSocket;
    private List<Client> connectedClients;
    private HashMap<String, List<Integer>> subscriberList;

    public Server() {
        port = 8000;
        shutdown = false;
    }

    public void terminateServer() {
        shutdown = true;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            connectedClients = new ArrayList<>();
            subscriberList = new HashMap<>();

            while(!shutdown) {
                // wait on client connection
                socket = serverSocket.accept();

                // manage client connection
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
