package Server;

import Messages.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

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

    @Override
    public void run() {
        try {
            while(keepPublishing) {
                Packet p = requests.take();

                try {
                    switch (p.getType()) {
                        case "REG-MSG":
                            RegistrationMsg registrationMsg = (RegistrationMsg) p.getData();

                            for (String channel : registrationMsg.getSubscribedChannels()) {
                                history.get(channel).add(registrationMsg);

                                for (Client client : subscribers.get(channel)) {

                                    // Send the packet with an appropriate message if the client is currently on this channel
                                    if (client.getCurrentChannel().equals(channel)) {
                                        // If this client is the person who just registered, send them the history of the channel they're currently on
                                        if (registrationMsg.getUsername().equals(client.getName())) {
                                            ChangeChannelMsg changeChannelMsg = new ChangeChannelMsg(channel);
                                            changeChannelMsg.setChatHistory(history.get(channel));
                                            client.getOut().writeObject(new Packet("CNG-MSG", changeChannelMsg));
                                        }
                                        // If this client is not the person who just registered, just send them the message of who registered
                                        else {
                                            client.getOut().writeObject(p);
                                        }
                                    }
                                }
                            }
                            break;

                        case "TXT-MSG":
                            ChannelMsg channelMsg = (ChannelMsg) p.getData();
                            String txtChannel = channelMsg.getPublishToChannel();

                            history.get(txtChannel).add(channelMsg);

                            for (Client client : subscribers.get(txtChannel)) {
                                // Send the packet with an appropriate message if the client is currently on this channel
                                if (client.getCurrentChannel().equals(txtChannel)) {
                                    client.getOut().writeObject(p);
                                }
                            }
                            break;

                        case "PIC-MSG" :
                            PictureMsg pictureMsg = (PictureMsg) p.getData();
                            String picChannel = pictureMsg.getPublishToChannel();

                            history.get(picChannel).add(pictureMsg);

                            for (Client client : subscribers.get(picChannel)) {
                                // Send the packet with an appropriate message if the client is currently on this channel
                                if (client.getCurrentChannel().equals(picChannel)) {
                                    client.getOut().writeObject(p);
                                }
                            }
                            break;

                        case "CNG-MSG" :
                            ChangeChannelMsg changeChannelMsg = (ChangeChannelMsg) p.getData();

                            changeChannelMsg.setChatHistory(history.get(changeChannelMsg.getSwappedChannel()));

                            synchronized (clients) {
                                for (Client client : clients) {
                                    // If this is the client who wants to change channels, send th
                                    if (client.getName().equals(changeChannelMsg.getSender())) {
                                        client.getOut().writeObject(p);
                                        break; //If the client who wants to change their channel is found, exit the loop
                                    }
                                }
                            }
                            break;

                        default :
                            System.out.println("RequestHandler - ERROR (No matching message type)");
                    }
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch(InterruptedException e) {
            System.out.println("RequestHandler INTERRUPTED");
        }
    }
}
