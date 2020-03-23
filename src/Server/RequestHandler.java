package Server;

import Messages.Packet;
import Messages.RegistrationMsg;

import javax.imageio.spi.RegisterableService;
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

                switch(p.getType()) {
                    case "REG-MSG" :
                        RegistrationMsg registrationMsg = (RegistrationMsg) p.getData();

                        for(String channel : registrationMsg.getSubscribedChannels()) {
                            history.get(channel).add(registrationMsg);

                            for(Client client : subscribers.get(channel)) {
                                if()
                            }
                        }
                }
            }
        }
        catch(InterruptedException e) {
            System.out.println("RequestHandler INTERRUPTED");
        }
    }
}
