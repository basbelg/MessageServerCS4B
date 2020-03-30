package Messages;

import java.io.Serializable;

public class NewUserMsg implements Serializable
{
    private String newUser;
    private String toChannel;

    public NewUserMsg(String newUser, String toChannel)
    {
        setNewUser(newUser);
        setToChannel(toChannel);
    }

    public String getNewUser() {
        return newUser;
    }

    public String getToChannel() {
        return toChannel;
    }

    public void setNewUser(String newUser) {
        this.newUser = newUser;
    }

    public void setToChannel(String toChannel) {
        this.toChannel = toChannel;
    }
}
