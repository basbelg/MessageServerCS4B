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
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Server implements Runnable {
    private BlockingQueue<Packet> requests;
    private int port;
    private boolean shutdown;
    private Socket socket;
    private ServerSocket serverSocket;
    private List<Client> clients;
    private Map<String, List<Client>> subscribers;
    private Map<String, List<Serializable>> history;
    private RequestHandler serverPublishThread;
    private Controller controller;

    public Server(int port) {
        this.port = port;
        shutdown = false;
    }

    public void terminateServer() {shutdown = true;}

    public void addChannel(String channel) {
        subscribers.putIfAbsent(channel, synchronizedList(new ArrayList<>()));
    }

    public void removeChannel(String channel) {subscribers.remove(channel);}

    public int getPort() {return port;}

    @Override
    public void run() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            try {
                Pane p = fxmlLoader.load(getClass().getResource("ServerUI.fxml").openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = (Controller) fxmlLoader.getController();

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

                // manage client connection
                clients.add(new Client(socket, requests, clients, subscribers, controller));
                controller.setConnectedClients(clients.size());
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            for(Client client: clients)
                client.terminateConnection();
        }
    }
}
