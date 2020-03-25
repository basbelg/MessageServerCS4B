package Messages;

import java.io.Serializable;
import java.util.List;

public class ChangeChannelMsg implements Serializable {
    private String swappedChannel;
    private List<Serializable> chatHistory;
    private String sender;

    public ChangeChannelMsg(String swappedChannel) {
        this.swappedChannel = swappedChannel;
        this.chatHistory = null;
        this.sender = "";
    }

    public String getSwappedChannel() {
        return swappedChannel;
    }

    public List<Serializable> getChatHistory() {
        return chatHistory;
    }

    public String getSender() {
        return sender;
    }

    public String toString() {return sender + " moved to " + swappedChannel;}

    public void setChatHistory(List<Serializable> chatHistory) { this.chatHistory = chatHistory; }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
