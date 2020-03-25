package Messages;

import java.io.Serializable;
import java.util.List;

public class RegistrationMsg implements Serializable {
    private String username;
    private String startingChannel;
    private List<String> subscribedChannels;

    public RegistrationMsg(String username, String startingChannel, List<String> subscribedChannels) {
        this.username = username;
        this.startingChannel = startingChannel;
        this.subscribedChannels = subscribedChannels;
    }

    public String getUsername() {
        return username;
    }

    public String getStartingChannel() {
        return startingChannel;
    }

    public String toString() {return username + " subscribed to " + startingChannel;}

    public List<String> getSubscribedChannels() {
        return subscribedChannels;
    }
}
