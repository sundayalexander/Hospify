package com.sunday.controller;


import com.sunday.hotspot.Hotspot;
import com.sunday.hotspot.TrayIconAdapter;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;


public class SplashScreenController implements ControllerAdapter, Initializable {
    private ScreenController controller;
    private Service<String> backgroundService;
    private TrayIconAdapter trayIcon;
    public final static String FILE_PATH = System.getenv("LOCALAPPDATA") + "/Hospify/";


    @FXML
    private Label status;

    public SplashScreenController(){
        //Create and start the background service
         this.backgroundService = new Service<>() {
            @Override
            protected Task createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        this.updateMessage("Configuring Required Hardware...");

                        //Configure required hardware
                        var dir = new File(SplashScreenController.FILE_PATH);
                        var file = new File(SplashScreenController.FILE_PATH+"setting.ini");
                        var prop = new Properties();
                        try {
                            if(!dir.exists()) {
                                dir.mkdir();
                                file.createNewFile();
                                file = new File(SplashScreenController.FILE_PATH+"log.log");
                                file.createNewFile();
                            }else if(!file.exists()){
                                file.createNewFile();
                                file = new File(SplashScreenController.FILE_PATH+"log.log");
                                file.createNewFile();
                            }
                            prop.load(new FileInputStream(SplashScreenController.FILE_PATH+"setting.ini"));
                        } catch (IOException e) {
                            this.updateMessage(e.getMessage());
                        }
                        Thread.sleep(4000);
                        this.updateMessage("Hardware Configured Successfully...");
                        return "success";
                    }
                };
            }
        };




    }

    /**
     * This method is used to set the
     * screen's parent for the current
     * view
     *
     * @param screenPage this is the
     *                   screen page.
     */
    @Override
    public void setScreenParent(ScreenController screenPage) {
        this.controller = screenPage;
    }

    /**
     * This method sets the display tray icon
     * @param trayIcon window icon
     */
    @Override
    public void setDisplayTrayIcon(TrayIconAdapter trayIcon) {
        this.trayIcon = trayIcon;
    }

    /**
     * This method set the hotspot.
     *
     * @param hotspot window hotspot
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
        this.status.textProperty().bind(this.backgroundService.messageProperty());
        this.backgroundService.setOnSucceeded(e->this.nextScene());
        this.backgroundService.start();

    }

    /**
     * This method transit the app to the next
     * scene.
     */
    private void nextScene(){
        var file = new File(SplashScreenController.FILE_PATH+"setting.ini");
        if(file.exists()){
            Properties prop = new Properties();
            try {
                prop.load(new FileInputStream(file));
                if(prop.isEmpty()){
                    this.controller.setScreen("hotspotConfig");
                }else{
                    this.controller.setScreen("hotspotService");
                }
            } catch (IOException e) {
                this.trayIcon.addNofication("Hospify 2018 - Hardware configuration status",
                        e.getLocalizedMessage(),TrayIconAdapter.ERROR);
            }
        }else{
            this.controller.setScreen("hotspotConfig");
        }
    }

}
