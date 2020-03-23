package Server;

import Messages.ChangeChannelMsg;
import Messages.ChannelMsg;
import Messages.Packet;
import Messages.RegistrationMsg;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class RequestHandler implements Runnable {
    private BlockingQueue<Packet> requests;
    private List<Client> clients;
    private HashMap<String, List<Client>> subscribers;
    private HashMap<String, List<Serializable>> history;
    private Thread publishThread;
    private boolean keepPublishing;

    public RequestHandler(BlockingQueue<Packet> requests, List<Client> clients, HashMap<String, List<Client>> subscribers,
                          HashMap<String, List<Serializable>> history) {
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

                                    // Send the appropriate message if the client is currently on this channel
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
                        String channel = channelMsg.getPublishToChannel();

                        history.get(channel).add(channelMsg);

                        for (Client client : subscribers.get(channel)) {
                            // Send the appropriate message if the client is currently on this channel
                            if (client.getCurrentChannel().equals(channel)) {
                                client.getOut().writeObject(p);
                            }
                        }
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
