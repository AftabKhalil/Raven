package network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aftab
 */
public class Server {

    private InetAddress LocalAdress;
    private String IPAdress;
    private InetSocketAddress serverSocketAdress;
    private ServerSocket serverSocket;
    private int port;
    private int concurrentClients;
    private int currentClients;
    public static LinkedList Receivers;

    private ClientAcceptor clientAcceptor;

    public Server(int port, int concurrentClients) throws IOException {
        this.port = port;
        this.concurrentClients = concurrentClients;
        this.currentClients = 0;
        Receivers = new LinkedList();

        LocalAdress = InetAddress.getLocalHost();
        IPAdress = LocalAdress.getHostAddress();

        serverSocketAdress = new InetSocketAddress(LocalAdress, port);
        serverSocket = new ServerSocket();
        serverSocket.bind(serverSocketAdress, concurrentClients);

        System.out.println("SERVER READY TO LISTEN AT : " + serverSocket.getLocalSocketAddress());

    }

    public void startAcceptingClients() {
        clientAcceptor = new ClientAcceptor();
        clientAcceptor.startAcceptingClients();
    }

    public String getIPAdress() {
        return IPAdress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getLocalAdress() {
        return LocalAdress;
    }

    public InetSocketAddress getServerSocketAdress() {
        return serverSocketAdress;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public int getPort() {
        return port;
    }

    public int getConcurrentClients() {
        return concurrentClients;
    }

    public int getCurrentClients() {
        return currentClients;
    }

    public void setConcurrentClients(int concurrentClients) {
        this.concurrentClients = concurrentClients;
    }

    public void setCurrentClients(int currentClients) {
        this.currentClients = currentClients;
    }

    public class Client {

    }

}
