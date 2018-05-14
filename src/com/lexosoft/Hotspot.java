/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lexosoft;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author Amowe Sunday Alexander
 * @version 1.0
 */
public class Hotspot extends GUI{
    private static EventHandler actionListener;
    private static Hotspot hotspot;
    private static Thread splashScreen;
    private JTextField hotspotName;
    private JPasswordField password;
    private JComboBox internetList;
    private StringTokenizer networksAdapters;
    private JCheckBox showPassword;
    private JTextField visiblePassword;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            Hotspot.actionListener = new EventHandler();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Hotspot.hotspot = new Hotspot();

        //Display Flash screen
        splashScreen = new Thread(()->Hotspot.hotspot.splashScreen());
        splashScreen.start();

        //Load frame content in background.
        Thread initContent = new Thread(()->Hotspot.hotspot.addContent());
        initContent.start();
        //Join the main thread
        try {
            splashScreen.join(2000);
        } catch (InterruptedException ex) {
            Hotspot.hotspot.getStatusLabel().setText(ex.getMessage());
        }
    }

    /**
     * This is an overriden method
     * that overide addcontent method
     * in GUI.java class.
     * It add components to the panels
     */
    public void addContent(){
        this.getServicePanel().setBackground(Color.WHITE);
        this.hotspotName = new JTextField("Hospify_v1");
        this.password = new JPasswordField("Hospify21@12");
        this.showPassword = new JCheckBox();
        this.showPassword.setText("Show Password");
        this.showPassword.setBackground(Color.WHITE);
        this.password.setToolTipText("Password must be 8-64 characters long.");
        this.hotspotName.setToolTipText("Multiple hotspot name should be sparated with an hiphen(-) or underscore(_)");
        //this.password.setText(Hotspot.actionListener.getHotspotKey());
        JButton connect = new JButton("Start Hotspot");
        JLabel sharedInternet = new JLabel("Internet to Share");
        JLabel hotspotLabel = new JLabel("Hotspot Name");
        JLabel passwordLabel = new JLabel("Password");
        hotspotLabel.setFont(new Font("arial", Font.BOLD, 14));
        passwordLabel.setFont(new Font("arial", Font.BOLD, 14));
        hotspotName.setFont(new Font("arial", Font.PLAIN, 12));
        this.internetList = new JComboBox();
        internetList.setFont(new Font("arial", Font.PLAIN, 12));
        password.setFont(new Font("arial", Font.PLAIN, 12));
        internetList.addItem("No Internet Sharing");

        //Get list of adapters from EventHandler.
        try {
            Iterator<String> adapters = Hotspot.actionListener.getAdapters().iterator();
            while (adapters.hasNext()){
                internetList.addItem(adapters.next());
            }
        } catch (IOException e) {
            getStatusLabel().setText(e.getMessage());
        }

        GridBagConstraints constraints = new  GridBagConstraints();
        constraints.insets = new Insets(20,10,2,10);
        this.getServicePanel().setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.ipadx = 19;
        constraints.ipady = 10;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 2;
        constraints.gridheight = 1;
        constraints.weightx = 0.2;
        constraints.weighty = 0.02;
        sharedInternet.setFont(new Font("arial", Font.BOLD, 14));
        this.getServicePanel().add(sharedInternet, constraints);
        //Add internet list combobox to the pane
        constraints.gridy = 1;
        constraints.insets = new Insets(-10,10,10,10);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        this.getServicePanel().add(internetList, constraints);
        //Add hotspotName to the pane
        constraints.weighty = 0.03;
        constraints.insets = new Insets(13,10,0,10);
        constraints.gridy = 2;
        constraints.gridheight = 1;
        this.getServicePanel().add(hotspotLabel, constraints);
        constraints.gridy = 3;
        constraints.insets = new Insets(-13,10,10,10);
        this.getServicePanel().add(hotspotName, constraints);
        //Add Password field to the pane
        this.visiblePassword = new JTextField();
        constraints.weighty = 0.06;
        constraints.insets = new Insets(5,10,0,10);
        constraints.gridy = 2;
        constraints.gridheight = 1;
        constraints.gridy = 4;
        this.getServicePanel().add(passwordLabel, constraints);
        constraints.weighty = 0.01;
        constraints.insets = new Insets(-20,10,10,10);
        constraints.gridy = 5;
        this.getServicePanel().add(password, constraints);
        this.getServicePanel().add(this.visiblePassword, constraints);

        //Add show password checkbox and label to the pane
        constraints.gridy = 6;
        constraints.gridx = 0;
        constraints.insets = new Insets(-10,10,30,10);
        this.getServicePanel().add(this.showPassword, constraints);
        this.showPassword.addActionListener(Hotspot.actionListener.togglePasswordPane());
        Hotspot.actionListener.initializePwdCheckbox(showPassword);
        //Add start hotspot button to pane
        constraints.insets = new Insets(70,20,2,20);
        constraints.gridy = 7;
        constraints.gridheight = 6;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        this.getServicePanel().add(connect, constraints);
        //connect.setText(Hotspot.actionListener.getConnectionStatus());
        connect.addActionListener(e -> {
            if(e.getActionCommand().trim().equals("Stop Hotspot")){
                Hotspot.actionListener.stopHotspot(connect, this.getStatusLabel());
            }else{
                Hotspot.actionListener.startHotspot(hotspotName.getText(), this.getTappedPane(), this.password,
                        connect,this.visiblePassword, this.getStatusLabel());

            }
        });

        this.setUsersContent();
        Hotspot.actionListener.initApp(this.getStatusLabel(), connect, this.visiblePassword,
                this.password, this.getTappedPane(), hotspotName);
    }

    /**
     * This method adds component
     * to the user's panel
     */
    private void setUsersContent(){
        this.getUserPanel().setBackground(Color.WHITE);
        GridBagConstraints constraints = new  GridBagConstraints();
        constraints.insets = new Insets(20,10,2,10);
        this.getUserPanel().setLayout(new GridBagLayout());
    }



}
