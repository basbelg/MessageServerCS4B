package Messages;

import java.io.Serializable;

public class Packet implements Serializable {
    public String type;
    public Serializable value;

    public Packet(String type, Serializable value) {
        this.type = type;
        this.value = value;
    }
}
