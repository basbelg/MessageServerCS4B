package Server;

import Messages.Packet;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.synchronizedMap;

public class Server implements Runnable {
    private BlockingQueue<Packet> requests;
    private int port;
    private int numOfClients;
    private boolean shutdown;
    private Thread thread;
    private Socket socket;
    private ServerSocket serverSocket;
    private List<Client> clients;
    private Map<String, List<Client>> subscribers;
    private Map<String, List<Serializable>> history;
    private RequestHandler serverPublishThread;
    private Controller controller;

    public Server(int port) {
        this.port = port;
        numOfClients = 0;
        shutdown = false;

        thread = new Thread(this);
        thread.start();
    }

    public synchronized void terminateServer() {
        shutdown = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChannel(String channel) {
        subscribers.putIfAbsent(channel, synchronizedList(new ArrayList<>()));
    }

    public void removeChannel(String channel) {subscribers.remove(channel);}

    public int getPort() {return port;}

    @Override
    public void run() {
        try {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.load(getClass().getResource("ServerUI.fxml").openStream());
                controller = (Controller) fxmlLoader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverSocket = new ServerSocket(port);
            requests = new ArrayBlockingQueue<>(512);
            clients = synchronizedList(new ArrayList<Client>());
            subscribers = synchronizedMap(new HashMap<>());
            history = synchronizedMap(new HashMap<>());

            String[] courses = {"CS1A", "CS1B", "CS4A", "CS4B", "CS3A", "CS3B"};
            for(int i = 0; i < courses.length; ++i) {
                subscribers.put(courses[i], synchronizedList(new ArrayList<>()));
                history.put(courses[i], synchronizedList(new ArrayList<>()));
            }

            serverPublishThread = new RequestHandler(requests, clients, subscribers, history);

            while(!shutdown) {
                // wait on client connection
                socket = serverSocket.accept();
                System.out.println("socket accepted: " + socket.toString());

                // manage client connection
                // elements duplicated here for an unknown reason
                clients.add(/*numOfClients,*/ new Client(socket, requests, clients, subscribers, controller));

//                System.out.println(numOfClients);
//                System.out.println(clients.size());
//                synchronized (clients) {
//                    Iterator i = clients.iterator();
//                    while(i.hasNext())
//                        System.out.println(((Client) i.next()).getSocket().toString());
//                }

                controller.setConnectedClients(++numOfClients);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("server thread terminated");
            synchronized (clients) {
                Iterator i = clients.iterator();
                while(i.hasNext())
                    ((Client)i.next()).terminateConnection();
            }
            serverPublishThread.getPublishThread().interrupt();
        }
    }
}
