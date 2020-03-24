package Server;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class Controller {
    public Label serverIP;
    public Label serverStatus;
    public Label serverPort;
    public Label connectedClients;
    public ListView backlog;

    public void terminatePressed(MouseEvent mouseEvent) {
    }

    public void launchPressed(MouseEvent mouseEvent) {
        Server server = new Server();
        new Thread(server).start();

        serverPort.setText(server.getPort() + "");
        serverIP.setText(server.getIP().toString());
        serverStatus.setText("Online");
    }
}
