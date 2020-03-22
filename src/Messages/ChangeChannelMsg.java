package Messages;

import java.util.List;

public class ChangeChannelMsg {
    private String swappedChannel;
    List<ChannelMsg> chatHistory;

    public ChangeChannelMsg(String swappedChannel) {
        this.swappedChannel = swappedChannel;
        this.chatHistory = null;
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
