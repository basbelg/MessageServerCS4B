package Messages;

public class ChannelMsg {
    String textMsg;
    String publishToChannel;
    String sender;

    public ChannelMsg() {
        this("<UNKNOWN>", "<UNKNOWN>", "<UNKNOWN>");
    }

    public ChannelMsg(String textMsg, String publishToChannel, String sender) {
        this.textMsg = textMsg;
        this.publishToChannel = publishToChannel;
        this.sender = sender;
    }

    public String getTextMsg() {
        return textMsg;
    }
}
