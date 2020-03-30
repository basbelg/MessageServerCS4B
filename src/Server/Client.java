package Server;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import Messages.*;

import static java.util.Collections.synchronizedList;

public class Client implements Runnable {
    // client info
    private String name;
    private String currentChannel;
    private List<String> channels;
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

    public Client(Socket socket, BlockingQueue<Packet> requests, List<Client> clients, Map<String, List<Client>> subscribers, Controller controller) {
        try {
            // set client info
            this.name = "guest";
            this.count = count;
            channels = new ArrayList<>();

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
        channels.add(channel);
        subscribers.get(channel).add(this);
    }

    public void removeSubscription(String channel) {
        channels.remove(channel);
        subscribers.get(channel).remove(this);
    }

    public final boolean isSubscribed(String channel) {return channels.contains(channel);}

    public Thread getClientThread() {return clientThread;}

    public Socket getSocket() {return socket;}

    public String getName() {
        return name;
    }

    public String getCurrentChannel() {
        return currentChannel;
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
                        //channels = registrationMsg.getChannels();
                        //for (String channel : channels)
                        //    subscribers.get(channel).add(this);
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

                        // dont need a change channel message anymore
                   /* case "CNG-MSG":
                        ChangeChannelMsg changeChannelMsg = (ChangeChannelMsg) p.getData();
                        changeChannelMsg.setSender(name);
                        currentChannel = changeChannelMsg.getSwappedChannel();
                        controller.printMessage(p.getData().toString());
                        break;*/

                    case "JNC-MSG":
                        JoinChannelMsg joinChannelMsg = (JoinChannelMsg) p.getData();
                        joinChannelMsg.setSender(name);
                        subscribers.get(joinChannelMsg.getJoinChannel()).add(this);
                        controller.printMessage(p.getData().toString());
                        break;

                    case "CRT-MSG":
                        CreateChannelMsg createChannelMsg = (CreateChannelMsg) p.getData();
                        createChannelMsg.setChannelOwner(name);
                        List<Client> temp = synchronizedList(new ArrayList<>());
                        temp.add(this);
                        subscribers.put(createChannelMsg.getChannelName(), temp);
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
                for(String channel: channels)
                    subscribers.get(channel).remove(this);
            }
        }
    }
}
