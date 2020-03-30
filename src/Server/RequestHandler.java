package Server;

import Messages.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static java.util.Collections.synchronizedList;

public class RequestHandler implements Runnable {
    private BlockingQueue<Packet> requests;
    private List<Client> clients;
    private Map<String, List<Client>> subscribers;
    private Map<String, List<Serializable>> history;
    private Thread publishThread;
    private boolean keepPublishing;

    public RequestHandler(BlockingQueue<Packet> requests, List<Client> clients, Map<String, List<Client>> subscribers,
                          Map<String, List<Serializable>> history) {
        this.requests = requests;
        this.clients = clients;
        this.subscribers = subscribers;
        this.history = history;

        keepPublishing = true;
        publishThread = new Thread(this);
        publishThread.start();
    }

    public Thread getPublishThread() {
        return publishThread;
    }

    @Override
    public void run() {
        try {
            while(!publishThread.isInterrupted()) {
                Packet p = requests.take();

                try {
                    switch (p.getType()) {
//                        case "REG-MSG":
//                            RegistrationMsg registrationMsg = (RegistrationMsg) p.getData();
//
//                            synchronized (history) {
//                                for (String channel : registrationMsg.getChannels()) {
//                                    history.get(channel).add(registrationMsg);
//
//                                    for (Client client : subscribers.get(channel)) {
//                                        // Send the packet with an appropriate message if the client is currently on this channel
//                                        if (client.getCurrentChannel().equals(channel)) {
//                                            client.getOut().writeObject(p);
//
//                                            // If this client is the person who just registered, send them the history of the channel they're currently on
//                                            if (registrationMsg.getUsername().equals(client.getName())) {
//                                                ChangeChannelMsg changeChannelMsg = new ChangeChannelMsg(channel);
//                                                changeChannelMsg.setChatHistory(history.get(channel));
//                                                client.getOut().writeObject(new Packet("CNG-MSG", changeChannelMsg));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            break;
//
//                            case "NWU-MSG":
//                            NewUserMsg ccm = (NewUserMsg) p.getData();
//                            break;

                        case "REG-MSG":
                        case "NWU-MSG":
                            break;

                        case "JNC-MSG":
                            JoinChannelMsg joinChannelMsg = (JoinChannelMsg) p.getData();

                            // Add NewUserMsg (for this user) to history
                            NewUserMsg num = new NewUserMsg(joinChannelMsg.getSender(), joinChannelMsg.getJoinChannel());
                            history.get(joinChannelMsg.getJoinChannel()).add(num);

                            // Set chat history to be sent to client
                            joinChannelMsg.setChatHistory(history.get(joinChannelMsg.getJoinChannel()));

                            for (Client client : subscribers.get(joinChannelMsg.getJoinChannel()))
                                if(client.getName() == joinChannelMsg.getSender()) {    // send channel history to new member
                                    client.getOut().reset();
                                    client.getOut().writeObject(new Packet("JNC-MSG", joinChannelMsg));
                                }
                                else                                                    // send NewUserMsg to other clients in this channel
                                    client.getOut().writeObject(new Packet("NWU-MSG", num));
                            break;

                        case "CRT-MSG":
                            CreateChannelMsg createChannelMsg = (CreateChannelMsg) p.getData();

                            history.put(createChannelMsg.getChannelName(), synchronizedList(new ArrayList<>()));

                            for(Client client: clients)
                                client.getOut().writeObject(p);
                            break;

                        case "TXT-MSG":
                            ChannelMsg channelMsg = (ChannelMsg) p.getData();
                            String txtChannel = channelMsg.getPublishToChannel();

                            history.get(txtChannel).add(channelMsg);

                            // Send the packet with an appropriate message if the client is currently on this channel
                            for (Client client : subscribers.get(txtChannel))
                                if (client.getCurrentChannel().equals(txtChannel))
                                    client.getOut().writeObject(p);
                            break;

                        case "PIC-MSG":
                            PictureMsg pictureMsg = (PictureMsg) p.getData();
                            String picChannel = pictureMsg.getPublishToChannel();

                            synchronized (history) {
                                history.get(picChannel).add(pictureMsg);
                            }

                            // Send the packet with an appropriate message if the client is currently on this channel
                            for (Client client : subscribers.get(picChannel))
                                if (client.getCurrentChannel().equals(picChannel))
                                    client.getOut().writeObject(p);
                            break;

                            // Unnecessary
//                        case "CNG-MSG":
//                            ChangeChannelMsg changeChannelMsg = (ChangeChannelMsg) p.getData();
//                            changeChannelMsg.setChatHistory(history.get(changeChannelMsg.getSwappedChannel()));
//                            // If this is the client who wants to change channels, send th
//                            for (Client client : clients)
//                                if (client.getName().equals(changeChannelMsg.getSender())) {
//                                    client.getOut().reset();
//                                    client.getOut().writeObject(p);
//                                    break; //If the client who wants to change their channel is found, exit the loop
//                                }
//                            break;

                        default:
                            System.out.println("RequestHandler - ERROR (No matching message type)");
                    }
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        catch(InterruptedException e) {
            System.out.println("request handler thread terminated");
        }
    }
}
