package network;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static raven.Raven.server;

/**
 * @author aftab
 */
class ClientAcceptor implements Runnable {

    private final Thread clientAcceptor;
    private int c_no = 1;

    public ClientAcceptor() {
        this.clientAcceptor = new Thread(this);
    }

    public void startAcceptingClients() {
        this.clientAcceptor.start();
    }

    @Override
    public void run() {
        while (true) {
            if (server.getCurrentClients() < server.getConcurrentClients()) {
                Socket receiverSocket = null;
                try {
                    System.out.println("Waiting for client");
                    receiverSocket = server.getServerSocket().accept();

                    Object[] row = new Object[3];
                    row[0] = c_no;
                    row[1] = receiverSocket.getInetAddress();
                    row[2] = "Conected";

                    GUI.MainFrame.addRow(row);

                } catch (IOException ex) {
                    Logger.getLogger(ClientAcceptor.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (receiverSocket != null) {
                    ConnectedClient receiver = new ConnectedClient(c_no, receiverSocket);
                    server.Receivers.add(receiver);
                    server.setCurrentClients(server.getCurrentClients() + 1);
                    System.out.println("REQUEST RECEIVED FROM CLIENT : " + receiverSocket.getRemoteSocketAddress());
                    c_no++;
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientAcceptor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
