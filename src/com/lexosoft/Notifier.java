package com.lexosoft;

import com.lexosoft.Windows.CMD;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Properties;

/**
 * This is a Notifier class
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 5/16/2018 @12:02 PM
 */
public class Notifier {
    //Class properties goes here
    private CMD command;
    private boolean started;
    private boolean stop;
    private DisplayTrayIcon trayIcon;


    /**
     * Constructor
     */
    public Notifier(DisplayTrayIcon trayIcon, CMD cmd) throws IOException {
        //Constructor logic goes here
        this.command = cmd;
        this.started = false;
        this.stop = false;
        this.trayIcon = trayIcon;
    }

    /**
     * This method notify the user when a
     * new device is connected to the hotspot.
     */
    public void connectedDevice(){
        while (true){
            try {
                NetworkInterface hostedNetwork = NetworkInterface.getByName("wlan3");
                Iterator<InetAddress> it = hostedNetwork.getInetAddresses().asIterator();
                while (it.hasNext()){
                    System.out.println(it.next().getHostName());
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * This method notify users when the
     * status of the hotspot changes.
     */
    public void displayStatus() throws InterruptedException {
        while (true){
            Properties prop = this.command.getProperties();
            Boolean status = prop.getProperty("status")!= null?
                    prop.getProperty("status").equalsIgnoreCase("started"):false;
            if (!this.started && status){
                trayIcon.addNofication("Hotspot Started",
                        "Hotspot started successfully. Please ensure you enable internet" +
                                " sharing on your network adapter",DisplayTrayIcon.INFO);
                this.started = true;
                this.stop = false;
            }else if (!this.stop && !status){
                trayIcon.addNofication("Hotspot Not Started",
                        "The hotspot might have been stop or could not " +
                                "be started due to some driver errors.",
                        DisplayTrayIcon.WARNING);
                this.started = false;
                this.stop = true;
            }
            //System.out.println(this.stop);
            Thread.sleep(2000);
        }
    }
}
