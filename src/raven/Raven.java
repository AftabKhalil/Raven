package raven;

import GUI.MainFrame;
import GUI.ClientFrame;
import java.io.File;
import network.Client;
import network.Server;

/**
 * @author aftab
 */
public class Raven {
    
    public static Server server;
    public static Client client;
    public static File file;
    public static int portNo = 99;
    public static MainFrame mf;
    public static ClientFrame cf;
    
    public static void main(String[] args) {
        mf = new MainFrame();
        mf.setVisible(true);
        cf = new ClientFrame();
        cf.setVisible(true);
    }
}
