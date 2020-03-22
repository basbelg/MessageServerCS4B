package Messages;

import java.io.Serializable;

public class Packet implements Serializable {
    public String type;
    public Serializable data;

    public Packet(String type, Serializable value) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Serializable getValue() {
        return data;
    }
}
