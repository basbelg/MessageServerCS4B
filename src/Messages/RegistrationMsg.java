package Messages;

import java.io.Serializable;
import java.util.List;

public class RegistrationMsg implements Serializable {
    private String username;
    private List<String> subscribedChannels;

    public RegistrationMsg(String username, List<String> subscribedChannels) {
        this.username = username;
        this.subscribedChannels = subscribedChannels;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getSubscribedChannels() {
        return subscribedChannels;
    }
}
