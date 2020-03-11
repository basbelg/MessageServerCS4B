package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import Messages.Packet;

public class Client implements Runnable {
    private int index;
    private String name;
    private boolean connection;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public Client(int index, String name, Socket socket) {
        this.index = index;
        this.name = name;
        this.socket = socket;
        connection = true;
    }

    public int getIndex() {
        return index;
    }

    public void terminateConnection() {
        connection = false;
    }

    public void sendPacket(Packet p) {
        try {
            out = new DataOutputStream(socket.getOutputStream());

            // send packet to client
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());

            while(connection) {
                // serve client until client disconnects
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            // delete this object when it disconnects
        }

    }
}
