package com.sunday.controller;

import com.sunday.hotspot.CMDAdapter;
import com.sunday.hotspot.Hotspot;
import com.sunday.hotspot.TrayIconAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is a ConnectDeviceController class
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 9/17/2018 @11:11 AM
 */
public class ConnectDeviceController implements ControllerAdapter, Initializable {
    //Class properties goes here
    private CMDAdapter command;
    private SimpleStringProperty message;
    private TextField text;


    /**
     * Constructor
     */
    public ConnectDeviceController() {
        //Constructor logic goes here
        this.message = new SimpleStringProperty();
//        try {
//            this.command = new CMDAdapter();
//        } catch (IOException e) {
//            this.message.set(e.getMessage());
//        }
    }

    /**
     * This method is used to set the
     * screen's parent for the current
     * view
     *
     * @param screenPage
     */
    @Override
    public void setScreenParent(ScreenController screenPage) {

    }

    /**
     * This method sets the display tray icon
     *
     * @param displayTrayIcon
     */
    @Override
    public void setDisplayTrayIcon(TrayIconAdapter displayTrayIcon) {

    }

    /**
     * This method set the hotspot.
     *
     * @param hotspot
     */
    @Override
    public void setHotspot(Hotspot hotspot) {

    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
