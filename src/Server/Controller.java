package Server;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class Controller {
    public Label serverIP;
    public Label serverStatus;
    public Label serverPort;
    public Label connectedClients;
    public ListView backlog;

    private Server server;
    private Integer port;

    public void printMessage(String message) {
        Platform.runLater(() -> {
            backlog.getItems().add(new Label(message));
        });
    }

    public void setConnectedClients(int num) {
        Platform.runLater(()-> {
            connectedClients.setText(num + "");
        });
    }

    public void terminatePressed(MouseEvent mouseEvent) {
        server.terminateServer();
        serverPort.setText("0");
        serverStatus.setText("Offline");
        connectedClients.setText("0");
    }

    public void launchPressed(MouseEvent mouseEvent) {
        port = (port == null)? 8000: port + 1;
        serverPort.setText(port + "");
        try {
            serverIP.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        serverStatus.setText("Online");
        server = new Server(port, this);
    }
}
