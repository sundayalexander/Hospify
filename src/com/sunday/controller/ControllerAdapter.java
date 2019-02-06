package com.sunday.controller;

import com.sunday.hotspot.TrayIconAdapter;
import com.sunday.hotspot.Hotspot;

/*This is a functional interface of
*Controller Adapter.
* */
public interface ControllerAdapter {

    /**
     * This method is used to set the
     * screen's parent for the current
     * view
     */
    void setScreenParent(ScreenController screenController);

    /**
     * This method sets the display tray icon
     * @param trayIcon
     */
    void setDisplayTrayIcon(TrayIconAdapter trayIcon);

    /**
     * This method set the hotspot.
     * @param hotspot
     */
    void setHotspot(Hotspot hotspot);

}
