package Messages;

import java.util.List;

public class RegistrationMsg {
    private String username;
    private List<String> subscribedChannels;

    public RegistrationMsg() {
        this("<UNKOWN>", null);
    }

    public RegistrationMsg(String username, List<String> subscribedChannels) {
        this.username = username;
        this.subscribedChannels = subscribedChannels;
    }
}
