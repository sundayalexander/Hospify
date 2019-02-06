package com.sunday.hotspot;

import java.awt.*;
import java.awt.event.ActionListener;

/**
 * This is a TrayIconAdapter class
 *
 * @author Amowe Sunday Alexander
 * @version v1.0
 * @date: 5/13/2018 @12:25 AM
 */
public class TrayIconAdapter {
    //Class properties goes here
    private TrayIcon trayIcon;
    private Image image;
    private SystemTray systemTray;
    private Boolean displayed;
    private PopupMenu menu;

    //Class constants
    public final static TrayIcon.MessageType WARNING = TrayIcon.MessageType.WARNING;
    public final static TrayIcon.MessageType INFO = TrayIcon.MessageType.INFO;
    public final static TrayIcon.MessageType ERROR = TrayIcon.MessageType.ERROR;
    public final static TrayIcon.MessageType NONE = TrayIcon.MessageType.NONE;


    /**
     * Constructor
     * @param image This is the tray icon
     * @param trayTitle This is the tray
     *                  tooltip text
     * @throws Exception This is thrown if
     * the system tray icon could not be
     * created.
     */
    public TrayIconAdapter(Image image, String trayTitle) throws Exception{
        this.displayed = false;
        this.image = image;
        //Constructor logic goes here
        if (!SystemTray.isSupported()){
            throw new Exception("Sorry! your system does not support Tray Icon");
        }
        this.trayIcon = new TrayIcon(this.image);
        this.trayIcon.setToolTip(trayTitle);
        this.systemTray = SystemTray.getSystemTray();
        this.menu = new PopupMenu();
        this.trayIcon.setImageAutoSize(true);
    }

    /**
     * This method removes the system
     * tray icon set earlier by this
     * class.
     */
    public void close(){
        this.systemTray.remove(this.trayIcon);
    }

    /**
     * This method returns true if the Tray
     * Icon has been added to the system tray
     * otherwise false
     * @return This is set to true if the tray
     * has been added to the system tray otherwise
     * false.
     */
    public Boolean isDisplayed(){
        return this.displayed;
    }

    /**
     * This method display the Tray icon on
     * the System
     * @throws AWTException This is thrown if
     * the created icon could not be added to
     * the system tray.
     */
    public void display() throws AWTException {
        if(this.menu.getItemCount() > 0){
            this.trayIcon.setPopupMenu(this.menu);
        }
        this.systemTray.add(this.trayIcon);
        this.displayed = true;
    }

    /**
     * This method add menu item to the tray
     * icon.
     * @param title This is the name of the
     *              menu item.
     * @param listener This is the menu item
     *                 action listener
     */
    public void addMenu(String title, ActionListener listener){
        if(this.menu.getItemCount()>0){
            this.menu.addSeparator();
        }
        MenuItem menuItem = new MenuItem(title);
        menuItem.addActionListener(listener);
        this.menu.add(menuItem);

    }

    /**
     * This method adds notification message to the
     * the System Tray Icon.
     * @param caption This is the title of the message.
     * @param message This is the message body to be
     *                displayed.
     * @param type This is the type of message to display.
     */
    public void addNofication(String caption, String message, TrayIcon.MessageType type){
        this.trayIcon.displayMessage(caption,message,type);
    }

    /**
     * This method adds action listener to System tray icon.
     * @param listener This is the listener to be added.
     */
    public void addActionListener(ActionListener listener){
        this.trayIcon.addActionListener(listener);
    }

}
