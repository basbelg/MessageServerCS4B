package Messages;

public class PictureMsg {
    byte[] picData;
    String publishToChannel;
    String sender;

    public PictureMsg(byte[] picData, String publishToChannel) {
        this.picData = picData;
        this.publishToChannel = publishToChannel;
    }

    public byte[] getPicData() {
        return picData;
    }

    public String getPublishToChannel() {
        return publishToChannel;
    }

    public String getSender() {
        return sender;
    }

    public void setPicData(byte[] picData) {
        this.picData = picData;
    }

    public void setPublishToChannel(String publishToChannel) {
        this.publishToChannel = publishToChannel;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
