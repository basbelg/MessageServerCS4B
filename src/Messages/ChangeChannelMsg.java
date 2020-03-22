package Messages;

import java.io.Serializable;
import java.util.List;

public class ChangeChannelMsg implements Serializable {
    private String swappedChannel;
    private List<Serializable> chatHistory;
    private List<String> messageType;
    private String sender;

    public ChangeChannelMsg(String swappedChannel) {
        this.swappedChannel = swappedChannel;
        this.chatHistory = null;
        this.messageType = null;
        this.sender = "";
    }

    public String getSwappedChannel() {
        return swappedChannel;
    }

    public List<Serializable> getChatHistory() {
        return chatHistory;
    }

    public List<String> getMessageType() {
        return messageType;
    }

    public String getSender() {
        return sender;
    }

    public void setChatHistory(List<Serializable> chatHistory) {
        this.chatHistory = chatHistory;
    }

    public void setMessageType(List<String> messageType) {
        this.messageType = messageType;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
