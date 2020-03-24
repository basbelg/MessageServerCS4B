package Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import Messages.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class Client implements Runnable {
    // client info
    private String name;
    private String currentChannel;
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
    private Map<String, List<Client>> subscribers;

    public Client(Socket socket, BlockingQueue<Packet> requests, List<Client> clients, Map<String, List<Client>> subscribers) {
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

    public String getName() {
        return name;
    }

    public String getCurrentChannel() {
        return currentChannel;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void terminateConnection() {isConnected = false;}

    @Override
    public void run() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try {
            Pane p = fxmlLoader.load(getClass().getResource("ServerUI.fxml").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Controller controller = (Controller) fxmlLoader.getController();

        try {
            while(isConnected) {
                // serve client until client disconnects
                Packet p = (Packet) in.readObject();

                switch(p.getType()) {
                    case "REG-MSG" :
                        RegistrationMsg registrationMsg = (RegistrationMsg) p.getData();

                        name = registrationMsg.getUsername();
                        currentChannel = registrationMsg.getStartingChannel();
                        channels = registrationMsg.getSubscribedChannels();

                        for(String channel : channels) {
                            subscribers.get(channel).add(this);
                        }

                        controller.printMessage(registrationMsg.toString());
                        break;

                    case "TXT-MSG" :
                        ((ChannelMsg) p.getData()).setSender(name);
                        controller.printMessage(p.getData().toString());
                        break;

                    case "PIC-MSG" :
                        ((PictureMsg) p.getData()).setSender(name);
                        controller.printMessage(p.getData().toString());
                        break;

                    case "CNG-MSG" :
                        ChangeChannelMsg changeChannelMsg = (ChangeChannelMsg) p.getData();
                        changeChannelMsg.setSender(name);
                        currentChannel = changeChannelMsg.getSwappedChannel();
                        controller.printMessage(p.getData().toString());
                        break;

                    default :
                        System.out.println("ERROR");
                }

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
