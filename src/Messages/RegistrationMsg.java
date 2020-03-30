package Messages;

import java.io.Serializable;
import java.util.List;

public class RegistrationMsg implements Serializable {
    private String username;
    //private List<String> channels;

    public RegistrationMsg(String username/*, List<String> channels*/) {
        this.username = username;
        //this.channels = channels;
    }

    public String getUsername() {
        return username;
    }

    public String toString() {return username + " has connected";}

    //    public List<String> getChannels() {
    //        return channels;
    //    }
}
