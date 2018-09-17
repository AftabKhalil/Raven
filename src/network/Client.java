/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aftab
 */
public class Client {

    private String serverIP;
    private int serverPort;
    private InetAddress remoteIPAddress;
    private InetSocketAddress serverSocketAddress;
    private Socket clientSocket;
    private DataOutputStream output;
    private DataInputStream input;
    Listener listener;
    Sender sender;

    public Client(String serverIP, int serverPort) throws UnknownHostException, IOException {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        remoteIPAddress = InetAddress.getByName(serverIP);
        serverSocketAddress = new InetSocketAddress(remoteIPAddress, serverPort);
        connectToServer();
        System.out.println(clientSocket);
    }

    public final void connectToServer() throws IOException {
        clientSocket = new Socket();
        clientSocket.connect(serverSocketAddress);
        output = new DataOutputStream(clientSocket.getOutputStream());
        input = new DataInputStream(clientSocket.getInputStream());
        listener = new Listener();
        sender = new Sender();

        listener.listener.start();
        sender.sender.start();
    }

    public class Listener implements Runnable {

        Thread listener;

        public Listener() {
            listener = new Thread(this);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String message = input.readUTF();
                    String name;
                    System.out.println("RECEIVED: " + message + "\n");
                    if (message.compareTo("I_AM_SENDING_A_FILE") == 0) {
                        name = input.readUTF();
                        int noOfConnections = Integer.valueOf(input.readUTF());
                        FileReceiver fileReceivers[] = new FileReceiver[noOfConnections];
                        for (int i = 0; i < noOfConnections; i++) {
                            String ServerIPandPort = input.readUTF();
                            String IP = ServerIPandPort.split(":")[1];
                            int port = Integer.valueOf(ServerIPandPort.split(":")[2]);
                            int part = Integer.valueOf(ServerIPandPort.split(":")[0]);
                            fileReceivers[i] = new FileReceiver(part, IP, port);
                            fileReceivers[i].startReceiving();

                        }
                        for (int i = 0; i < noOfConnections; i++) {
                            fileReceivers[i].receiver.join();
                        }
                        System.out.println("WHOLE FILE RECEIVED");
                        GUI.ClientFrame.fileReceived.setText("MERGING");

                        File fileWrite = new File("E:\\received_"+name);
                        fileWrite.delete();
                        for (int i = 0; i < 10; i++) {
                            File fileRead = new File("E:\\" + i + ".txt");

                            FileInputStream fis = new FileInputStream(fileRead);
                            FileOutputStream fos = new FileOutputStream(fileWrite, true);

                            for (int j = 0; j < fileRead.length(); j++) {
                                int a = fis.read();
                                fos.write(a);
                            }

                            fis.close();
                            fos.close();
                        }
                        GUI.ClientFrame.fileReceived.setText("SAVED AS "+fileWrite);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public class Sender implements Runnable {

        String message;
        Thread sender;
        Scanner in;

        public Sender() {
            sender = new Thread(this);
            in = new Scanner(System.in);
            try {
                output = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException ex) {
                try {
                    output.close();
                    clientSocket.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.out.println(ex);
            }
        }

        @Override
        public void run() {
            while (true) {
                message = in.nextLine();
                try {
                    output.writeUTF(message);
                } catch (IOException ex) {
                    try {
                        input.close();
                        output.close();
                        clientSocket.close();
                    } catch (IOException ex1) {
                        System.out.println(ex);
                    }
                    System.out.println(ex);
                }
            }
        }
    }

    public class FileReceiver implements Runnable {

        private String serverIP;
        private int serverPort;
        private int part;
        private InetAddress remoteIPAddress;
        private InetSocketAddress serverSocketAddress;
        private Socket clientSocket;
        private DataInputStream input;
        Thread receiver;

        public FileReceiver(int part, String serverIP, int serverPort) {

            this.serverIP = serverIP;
            this.serverPort = serverPort;
            this.part = part;

            try {
                remoteIPAddress = InetAddress.getByName(serverIP);
                serverSocketAddress = new InetSocketAddress(remoteIPAddress, serverPort);
                clientSocket = new Socket();
                clientSocket.connect(serverSocketAddress, 5);
                input = new DataInputStream(clientSocket.getInputStream());
            } catch (UnknownHostException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            receiver = new Thread(this);
        }

        public void startReceiving() {
            this.receiver.start();
        }

        @Override
        public void run() {

            File file = new File("E://" + part + ".txt");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            int b = 0;
            long l;
            try {
                l = input.readLong();

                for (long i = 0; i <= l; i++) {
                    b = input.readByte();
                    fos.write(b);
                    //System.out.println(b);

                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
