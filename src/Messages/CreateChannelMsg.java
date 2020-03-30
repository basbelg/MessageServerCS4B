package Messages;

import java.io.Serializable;

public class CreateChannelMsg implements Serializable
{
    private String channelName;
    private String channelOwner;

    public CreateChannelMsg(String channelName)
    {
        setChannelName(channelName);
    }

    public void setChannelOwner(String channelOwner)
    {
        this.channelOwner = channelOwner;
    }

    private void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelName() { return channelName; }

    public String getChannelOwner() { return channelOwner; }

    @Override
    public String toString()
    {
        return (getChannelOwner() + " created new channel " + getChannelName() + '!');
    }
}
