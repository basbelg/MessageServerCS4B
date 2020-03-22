package Server;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.Collections.*;

import Messages.Packet;

public class Server implements Runnable {
    private BlockingQueue<Packet> requests;
    private int port;
    private boolean shutdown;
    private Socket socket;
    private ServerSocket serverSocket;
    private List<Client> clients;
    private HashMap<String, List<Client>> subscribers;
    private HashMap<String, List<Serializable>> history;
    private RequestHandler serverPublishThread;

    public Server() {
        port = 8000;
        shutdown = false;
    }

    public void terminateServer() {shutdown = true;}

    public void addChannel(String channel) {
        subscribers.putIfAbsent(channel, synchronizedList(new ArrayList<>()));
    }

    public void removeChannel(String channel) {subscribers.remove(channel);}

    public void messageChannel(String channel, Packet p) {
        for(Client client: subscribers.get(channel))
            client.sendPacket(p);
    }

    public void messageAllClients(Packet p) {
        for(Client client: clients)
            client.sendPacket(p);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            requests = new ArrayBlockingQueue<>(512);
            clients = synchronizedList(new ArrayList<Client>());
            subscribers = (HashMap) synchronizedMap(new HashMap<String, List<Client>>());
            history = (HashMap) synchronizedMap(new HashMap<String, List<Serializable>>());

            serverPublishThread = new RequestHandler(requests, clients, subscribers, history);

            while(!shutdown) {
                // wait on client connection
                socket = serverSocket.accept();

                // manage client connection
                clients.add(new Client(socket, requests, clients, subscribers));
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
