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
    private List<String> allChannels;
    private Thread publishThread;
    private boolean keepPublishing;

    public RequestHandler(BlockingQueue<Packet> requests, List<Client> clients, Map<String, List<Client>> subscribers,
                          Map<String, List<Serializable>> history, List<String> allchannels) {
        this.requests = requests;
        this.clients = clients;
        this.subscribers = subscribers;
        this.history = history;
        this.allChannels = allchannels;

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

                            synchronized (clients)
                            {
                                for (Client client : clients)
                                {
                                   if(client.getName().equals(((RegistrationMsg) p.getData()).getUsername()))
                                   {
                                       client.getOut().writeObject(p);
                                       break;
                                   }
                                }
                            }

                            break;

                        case "TXT-MSG":
                            ChannelMsg channelMsg = (ChannelMsg) p.getData();
                            String txtChannel = channelMsg.getPublishToChannel();

                            synchronized (history)
                            {
                                history.get(txtChannel).add(channelMsg);
                            }

                            // Send the packet with an appropriate message if the client is currently on this channel
                            for (Client client : subscribers.get(txtChannel))
                            {
                                client.getOut().writeObject(p);
                            }
                            break;

                        case "PIC-MSG":
                            PictureMsg pictureMsg = (PictureMsg) p.getData();
                            String picChannel = pictureMsg.getPublishToChannel();

                            synchronized (history) {
                                history.get(picChannel).add(pictureMsg);
                            }

                            // Send the packet with an appropriate message if the client is currently on this channel
                            for (Client client : subscribers.get(picChannel)) {
                                client.getOut().writeObject(p);
                            }
                            break;

                        case "CRT-MSG":
                            CreateChannelMsg ccm = (CreateChannelMsg) p.getData();
                            allChannels.add(ccm.getChannelName());
                            subscribers.put(ccm.getChannelName(), synchronizedList(new ArrayList<>()));
                            history.put(ccm.getChannelName(), synchronizedList(new ArrayList<>()));
                            NewUserMsg nm = new NewUserMsg(ccm.getChannelOwner(), ccm.getChannelName());
                            history.get(ccm.getChannelName()).add(nm);

                            for(Client client : clients)
                            {
                                client.getOut().writeObject(p);

                                if(client.getName().equals(ccm.getChannelOwner()))
                                {
                                    subscribers.get(ccm.getChannelName()).add(client);
                                    client.getOut().writeObject(new Packet("NWU-MSG", new NewUserMsg(ccm.getChannelOwner(), ccm.getChannelName())));
                                }
                            }
                            break;

                        case "JNC-MSG":


                            break;

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
