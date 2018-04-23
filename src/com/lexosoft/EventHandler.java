/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.lexosoft;

import com.lexosoft.Windows.CMD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 *
 * @author Amowe Sunday Alexander
 */
public class EventHandler {
    private final CMD cmd;
    private JButton connectBtn;
    private String ssid;
    private String maxUser;
    private JLabel statusLabel;
    private JTabbedPane tabbedPane;
    private JCheckBox pwdCheckBox;
    private JPasswordField passwordField;
    private JTextField visiblePassword;
    private String key;
    private JTextField ssidField;
    private GUI gui;


    /**
     * This is a Constructor method used the initialize the
     * windows CMD class.
     * @throws IOException
     */
    public EventHandler() throws IOException {

        this.cmd = new CMD();
    }

    /**
     * This method initialize the various components, required by the
     * Event Handler class.
     */
    public void initApp(JLabel statusLabel, JButton connectBtn, JTextField visiblePassword,
                        JPasswordField passwordField, JTabbedPane tabbedPane, JTextField ssidField){
        this.connectBtn = connectBtn;
        this.passwordField = passwordField;
        this.statusLabel = statusLabel;
        this.visiblePassword = visiblePassword;
        this.tabbedPane = tabbedPane;
        this.ssidField = ssidField;
        if(!this.cmd.getEnvirons().isEmpty()){
            this.ssidField.setText(this.cmd.getEnvirons().getProperty("ssid name").replace("\"",""));
            this.passwordField.setText(this.cmd.getEnvirons().getProperty("key"));
            this.statusLabel.setText("The hotspot is "+this.cmd.getProperties().getProperty("status"));
            String state = this.cmd.getProperties().getProperty("status").equalsIgnoreCase("started")?
                    "Stop Hotspot":"Start Hotspot";
            this.connectBtn.setText(state);
        }

    }

    /**
     * This method dispose every initialized frames and close the application.
     * @return ActionListener Object to the system
     * exit method.
     */
    public ActionListener exitApp(){
        return (ActionEvent e)->System.exit(0);
    }

    /**
     * * This method set up the initial parameter
     * And then starts the hotspot.
     * @param hotspotName This is a text field that house the
     *                    hotspot name.
     * @param tabbedPane This is a TabbedPane that is used to
     *                   switch between the various GUI pane.
     * @param password This is a password field that house the
     *                 connection password.
     * @param connectBtn This is a button object used to Initialize the
     *                   the connection.
     * @param visiblePassword This is used to display the user entered password.
     * @param statusLabel This Label Object is used to display the
     *                    status of the connection.
     * This method starts the hotspot network
     */
    public void startHotspot(String hotspotName, JTabbedPane tabbedPane, JPasswordField password,
                             JButton connectBtn, JTextField visiblePassword, JLabel statusLabel){
        //Set the various components
        this.connectBtn = connectBtn; this.ssid = hotspotName; this.tabbedPane = tabbedPane;
        this.statusLabel = statusLabel; this.passwordField = password;
        this.visiblePassword = visiblePassword; this.key = String.valueOf(password.getPassword());
        //get the stored key.
        String storedKey = this.cmd.getEnvirons().isEmpty()?"Hotspot12345":this.cmd.getEnvirons().getProperty("key");
        try {
            //Configure and start the hostednetwork.
            if(!this.ssid.equalsIgnoreCase(this.cmd.getProperties().getProperty("ssid name"))){
                if (this.cmd.setHostednetwork("allow",this.ssid, this.key)){
                    if(this.cmd.startHostednetwork()){
                        connectBtn.setText("Stop Hotspot");
                        this.statusLabel.setText("Hotspot started successfully...");
                        this.tabbedPane.setSelectedIndex(1);
                        Properties prop = this.cmd.getProperties();
                        prop.put("key", this.key);
                        FileOutputStream output = new FileOutputStream(this.cmd.getInitFile());
                        prop.store(output,
                                "Window Hotspot v1.0");
                    }else{
                        this.statusLabel.setText(this.cmd.getError());
                    }
                }else{
                    this.statusLabel.setText(this.cmd.getError());
                }

                //Reset the hotspot key/password.

            }else if(!this.key.equalsIgnoreCase(storedKey)){
                if(this.cmd.setHostednetwork("key",this.key)){
                    if(this.cmd.startHostednetwork()){
                        connectBtn.setText("Stop Hotspot");
                        this.statusLabel.setText("Hotspot started successfully...");
                        this.tabbedPane.setSelectedIndex(1);
                        Properties prop = this.cmd.getEnvirons();
                        prop.setProperty("key", this.key);
                        prop.store(new FileOutputStream(this.cmd.getInitFile()),
                                "Window Hotspot v1.0");
                    }else{
                        this.statusLabel.setText(this.cmd.getError());
                    }
                }else{
                    this.statusLabel.setText(this.cmd.getError());
                }
                //Start the hotspot from it previous state.
            }else if(this.cmd.getProperties().getProperty("mode").equalsIgnoreCase("Allowed") &&
                    this.cmd.getProperties().getProperty("status").equalsIgnoreCase("not started.")) {
                if(this.cmd.startHostednetwork()){
                    connectBtn.setText("Stop Hotspot");
                    this.statusLabel.setText("Hotspot started successfully...");
                    this.tabbedPane.setSelectedIndex(1);
                }else{
                    this.statusLabel.setText(this.cmd.getError());
                }
            }

        } catch (IOException e) {
            this.statusLabel.setText(e.getMessage());
        }

    }

    //This method stops the hotspot network
    public void stopHotspot(JButton connectBtn, JLabel statusLabel) {
        this.statusLabel = statusLabel;
        this.connectBtn = connectBtn;
        try {
            if(this.cmd.stopHostednetwork()){
                this.connectBtn.setText("Start Hotspot");
                this.statusLabel.setText("Hotspot stopped successfully...");
            }else{
                this.statusLabel.setText("Unable to stop hotspot");
            }
        } catch (IOException e) {
            this.statusLabel.setText(e.getMessage());
        }

    }

    /**
     * This method gets the password Check box from the gui
     * and initialize the passwordcheckbox in this class
     * @param pwdCheckBox This is the checkbox to
     *                    which the togglePasswordPane()
     *                    is attached to.
     */
    public void initializePwdCheckbox(JCheckBox pwdCheckBox){
        this.pwdCheckBox = pwdCheckBox;
    }

    /**
     * This method hides and shows the
     * users password in the password pane.
     * @return This is set to ActionListener
     * Object.
     */
    public ActionListener togglePasswordPane() {
        //Inner anonymous ActionListener Lambda
        //expression.
        return e -> {
            if(this.pwdCheckBox.isSelected()){
                this.visiblePassword.setVisible(true);
                this.visiblePassword.setText(String.valueOf(this.passwordField.getPassword()));
                this.passwordField.setVisible(false);
            }else{
                this.passwordField.setVisible(true);
                this.passwordField.setText(this.visiblePassword.getText());
                this.visiblePassword.setVisible(false);
            }
        };
    }

    /**
     * This method returns the list of adapters and their connection
     * state. That is connected or disconnected
     * @return This is the list of adapters which are formatted to
     * show their connection status
     * @throws IOException This is thrown if the method could not
     * perform basic IO operation.
     */
    public ArrayList<String> getAdapters() throws IOException {
        ArrayList<String> adapters = new ArrayList<>();
        Iterator<String> adapter = this.cmd.getAdapters().iterator();
        while(adapter.hasNext()){
            String[] options = adapter.next().split(" ");
            adapters.add(options[0]+" ("+options[2]+")");
        }
        return adapters;
    }
}

