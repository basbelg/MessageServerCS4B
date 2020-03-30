package Messages;

import java.io.Serializable;
import java.util.List;

public class JoinChannelMsg implements Serializable
{
    private String joinChannel;
    private String sender;
    private List<Serializable> chatHistory;

    public JoinChannelMsg(String joinChannel)
    {
        setJoinChannel(joinChannel);
    }

    public void setJoinChannel(String joinChannel) { this.joinChannel = joinChannel; }

    public void setSender(String sender) { this.sender = sender; }

    public void setChatHistory(List<Serializable> chatHistory) {this.chatHistory = chatHistory;}

    public String getJoinChannel() { return joinChannel; }

    public String getSender() { return sender; }

    public List<Serializable> getChatHistory() { return chatHistory; }

    @Override
    public String toString()
    {
        return (sender + " joined " + joinChannel);
    }
}
