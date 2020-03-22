package Messages;

import java.io.Serializable;

public class Packet implements Serializable {
    private String type;
    private Serializable data;

    public Packet(String type, Serializable data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public Serializable getData() {
        return data;
    }
}
