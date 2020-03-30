package Server;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import Messages.*;

public class Client implements Runnable {
    // client info
    private String name;
    private List<String> allChannels;
    private List<String> subscribedChannels;
    private int count;

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
    private Map<String, List<Client>> subscribers;

    private Controller controller;

    public Client(Socket socket, BlockingQueue<Packet> requests, List<Client> clients, Map<String, List<Client>> subscribers, Controller controller, List<String> allChannels) {
        try {
            // set client info
            this.name = "guest";
            this.count = count;
            subscribedChannels = new ArrayList<>();
            this.allChannels = allChannels;

            // set connection info
            isConnected = true;
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            // set server info
            this.controller = controller;
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

    public void addSubscription(String channel) {
        subscribedChannels.add(channel);
        subscribers.get(channel).add(this);
    }

    public void removeSubscription(String channel) {
        subscribedChannels.remove(channel);
        subscribers.get(channel).remove(this);
    }

    public final boolean isSubscribed(String channel) {return subscribedChannels.contains(channel);}

    public Thread getClientThread() {return clientThread;}

    public Socket getSocket() {return socket;}

    public String getName() {
        return name;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void terminateConnection() {
        clientThread.interrupt();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while(!clientThread.isInterrupted()) {
                // serve client until client disconnects
                Packet p = (Packet) in.readObject();

                switch (p.getType()) {
                    case "REG-MSG":
                        RegistrationMsg registrationMsg = (RegistrationMsg) p.getData();

                        name = registrationMsg.getUsername();
                        registrationMsg.setChannels(allChannels);

                        controller.printMessage(registrationMsg.toString());
                        break;

                    case "TXT-MSG":
                        ((ChannelMsg) p.getData()).setSender(name);
                        controller.printMessage(p.getData().toString());
                        break;

                    case "PIC-MSG":
                        ((PictureMsg) p.getData()).setSender(name);
                        controller.printMessage(p.getData().toString());
                        break;

                    case "CRT-MSG":
                        ((CreateChannelMsg) p.getData()).setChannelOwner(name);
                        controller.printMessage(p.getData().toString());
                        break;

                    case "JNC-MSG":
                        ((JoinChannelMsg) p.getData()).setSender(name);
                        subscribers.get(((JoinChannelMsg) p.getData()).getJoinChannel()).add(this);
                        controller.printMessage(p.getData().toString());
                        break;

                    default:
                        System.out.println("ERROR");
                }
                requests.add(p);
            }
        }
        catch(ClosedByInterruptException e) {
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            System.out.println("client removed: " + name);
            synchronized (clients) {
                clients.remove(this);
            }
            synchronized (subscribers) {
                for(String channel: subscribedChannels)
                    subscribers.get(channel).remove(this);
            }
        }
    }
}
