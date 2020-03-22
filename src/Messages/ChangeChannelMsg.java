package Messages;

import java.io.Serializable;
import java.util.List;

public class ChangeChannelMsg implements Serializable {
    private String swappedChannel;
    private List<ChannelMsg> chatHistory;
    private String sender;

    public ChangeChannelMsg(String swappedChannel) {
        this.swappedChannel = swappedChannel;
        this.chatHistory = null;
        this.sender = "";
    }

    public String getSwappedChannel() {
        return swappedChannel;
    }

    public List<ChannelMsg> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<ChannelMsg> chatHistory) {
        this.chatHistory = chatHistory;
    }
}
