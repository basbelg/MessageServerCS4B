package Messages;

public class ChannelMsg {
    String textMsg;
    String publishToChannel;
    String sender;

    public ChannelMsg(String textMsg, String publishToChannel, String sender) {
        this.textMsg = textMsg;
        this.publishToChannel = publishToChannel;
        this.sender = sender;
    }

    public String getTextMsg() {
        return textMsg;
    }

    public String getPublishToChannel() {
        return publishToChannel;
    }

    public String getSender() {
        return sender;
    }

    public void setTextMsg(String textMsg) {
        this.textMsg = textMsg;
    }

    public void setPublishToChannel(String publishToChannel) {
        this.publishToChannel = publishToChannel;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
