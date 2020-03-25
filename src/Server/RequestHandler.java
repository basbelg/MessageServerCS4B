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
                        case "REG-MSG":
                            RegistrationMsg registrationMsg = (RegistrationMsg) p.getData();

                            synchronized (history) {
                                for (String channel : registrationMsg.getSubscribedChannels())
                                    history.get(channel).add(registrationMsg);
                            }

                            for (Client client : subscribers.get(registrationMsg.getStartingChannel())) {
                                // Send the packet with an appropriate message if the client is currently on this channel
                                if (client.getCurrentChannel().equals(registrationMsg.getStartingChannel())) {
                                    client.getOut().writeObject(p);

                                    // If this client is the person who just registered, send them the history of the channel they're currently on
                                    if (registrationMsg.getUsername().equals(client.getName())) {
                                        ChangeChannelMsg changeChannelMsg = new ChangeChannelMsg(registrationMsg.getStartingChannel());
                                        changeChannelMsg.setChatHistory(history.get(registrationMsg.getStartingChannel()));
                                        client.getOut().writeObject(new Packet("CNG-MSG", changeChannelMsg));
                                    }
                                }
                            }
                            break;

                        case "TXT-MSG":
                            ChannelMsg channelMsg = (ChannelMsg) p.getData();
                            String txtChannel = channelMsg.getPublishToChannel();

                            synchronized (history) {
                                history.get(txtChannel).add(channelMsg);
                            }

                            // Send the packet with an appropriate message if the client is currently on this channel
                            for (Client client : subscribers.get(txtChannel))
                                if (client.getCurrentChannel().equals(txtChannel))
                                    client.getOut().writeObject(p);
                            break;

                        case "PIC-MSG" :
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

                        case "CNG-MSG" :
                            ChangeChannelMsg changeChannelMsg = (ChangeChannelMsg) p.getData();
                            System.out.println(history.get(changeChannelMsg.getSwappedChannel()).size());
                            changeChannelMsg.setChatHistory(history.get(changeChannelMsg.getSwappedChannel()));

                            // If this is the client who wants to change channels, send th
                            for (Client client : clients)
                                if (client.getName().equals(changeChannelMsg.getSender())) {
                                    client.getOut().writeObject(p);
                                    break; //If the client who wants to change their channel is found, exit the loop
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
            System.out.println("request handler thread terminated");
        }
    }
}
