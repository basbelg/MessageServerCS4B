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
}
