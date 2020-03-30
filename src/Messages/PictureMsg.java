package Messages;

import java.io.Serializable;

public class PictureMsg  implements Serializable {
    private byte[] picData;
    private String publishToChannel;
    private String sender;

    public PictureMsg(byte[] picData, String publishToChannel) {
        this.picData = picData;
        this.publishToChannel = publishToChannel;
        this.sender = "";
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

    public String toString() {return sender + " posted a picture in " + publishToChannel;}

    public void setPicData(byte[] picData) { this.picData = picData; }

    public void setPublishToChannel(String publishToChannel) {
        this.publishToChannel = publishToChannel;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
