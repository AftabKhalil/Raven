package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author aftab
 */
public class ConnectedClient {

    public int name;

    private DataOutputStream output;
    DataInputStream input;
    Socket clientSocket;
    Listener listner;

    /**
     *
     * @param name
     * @param receiverSocket
     */
    public ConnectedClient(int name, Socket receiverSocket) {
        this.name = name;
        this.clientSocket = receiverSocket;

        try {
            input = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            output = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ex) {
            System.out.println(ex);
        }
        startListner();
        //sendFile(new File("gsdgsd"), 20);
    }

    public final void startListner() {
        listner = new Listener();
        listner.listener.start();
    }

    public final void sendFile(File file, int noOfConnections) {

        long totalBytes = file.length();
        long size = totalBytes / noOfConnections;
        long start, end;

        try {
            output.writeUTF("I_AM_SENDING_A_FILE");
            output.writeUTF(file.getName());
            output.writeUTF(String.valueOf(noOfConnections));
        } catch (IOException ex) {
            Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        FileSender fileSenders[] = new FileSender[noOfConnections];
        for (int i = 0; i < noOfConnections; i++) {

            start = i * size;
            end = start + size - 1;
            if (i == noOfConnections - 1) {
                end = totalBytes-1;
            }

            fileSenders[i] = new FileSender(file, start, end);
            fileSenders[i].startSending();
            try {
                output.writeUTF(i + ":" + fileSenders[i].IPAdress + ":" + fileSenders[i].portNo);
            } catch (IOException ex) {
                Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public class Listener implements Runnable {

        private Thread listener;

        public Listener() {
            listener = new Thread(this);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String message = input.readUTF();
                    System.out.println(message);
                } catch (IOException ex) {
                    System.out.println(ex);
                    System.exit(0);
                }
            }
        }
    }

    public class FileSender implements Runnable {

        InetAddress LocalAdress;
        String IPAdress;
        int portNo;
        InetSocketAddress serverSocketAdress;
        ServerSocket serverSocket;
        Thread fileSender;
        Socket clientSocket;
        private DataOutputStream output;
        long startByte;
        long endByte;
        File file;

        public FileSender(File file, long startByte, long endByte) {
            fileSender = new Thread(this);
            this.startByte = startByte;
            this.endByte = endByte;
            this.file = file;
            try {
                LocalAdress = InetAddress.getLocalHost();
                IPAdress = LocalAdress.getHostAddress();

                raven.Raven.portNo++;
                portNo = raven.Raven.portNo;
                //System.out.println("I WANT TO CONNECT AT " + LocalAdress + " : " + portNo);
                serverSocketAdress = new InetSocketAddress(LocalAdress, portNo);

                serverSocket = new ServerSocket();
                serverSocket.bind(serverSocketAdress, 1);

            } catch (IOException ex) {
                Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void startSending() {
            this.fileSender.start();
        }

        @Override
        public void run() {
            try {
                clientSocket = serverSocket.accept();
                Thread.sleep(500);
            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                output = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException ex) {
                System.out.println(ex);
            }

            try {

                FileInputStream fis = new FileInputStream(file);

                for (int i = 0; i < startByte; i++) {
                    fis.read();
                }
                output.writeLong(endByte - startByte);
                for (int i = 0; i <= endByte - startByte; i++) {
                    int b = fis.read();
                    //System.out.println(b);
                    output.write(b);

                }

            } catch (IOException ex) {
                Logger.getLogger(ConnectedClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
