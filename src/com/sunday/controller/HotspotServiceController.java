package com.sunday.controller;

import com.jfoenix.controls.JFXButton;
import com.sunday.hotspot.Hotspot;
import com.sunday.hotspot.TrayIconAdapter;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
 * This is a HotspotServiceController class
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 9/17/2018 @6:14 PM
 */
public class HotspotServiceController implements ControllerAdapter, Initializable {
    //Class properties goes here
    private ScreenController controller;
    private Hotspot hotspot;
    private BufferedWriter logFile;
    private SimpleDoubleProperty opacity;
    private SimpleBooleanProperty hotspotStarted;
    private TrayIconAdapter trayIcon;

    //FX component declaration goes here.

    @FXML
    private Label statusPane;

    @FXML
    private Label deviceCount;

    @FXML
    private Label menuCaption;

    @FXML
    private JFXButton togglePowerBtn;


    @FXML
    private JFXButton hideBtn;

    @FXML
    private JFXButton closeBtn;

    @FXML
    private Label alertPane;

    @FXML
    private VBox menuBg;

    @FXML
    private VBox menuBar;

    @FXML
    private JFXButton settingMenu;

    @FXML
    private JFXButton devicesMenu;

    @FXML
    private JFXButton restartMenu;

    @FXML
    private JFXButton helpMenu;

    /**
     * Constructor
     */
    public HotspotServiceController() {


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
        this.controller = screenPage;
    }

    /**
     * This method sets the display tray icon
     *
     * @param trayIcon
     */
    @Override
    public void setDisplayTrayIcon(TrayIconAdapter trayIcon) {
        this.trayIcon = trayIcon;
    }

    /**
     * This method set the hotspot.
     *
     * @param hotspot
     */
    @Override
    public void setHotspot(Hotspot hotspot) {
        this.hotspot = hotspot;
        //Check status
        if(this.hotspot.getStatus().equalsIgnoreCase("started")){
            this.hotspotStarted.setValue(true);
        }
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
        this.opacity = new SimpleDoubleProperty(0.0);
        this.hotspotStarted = new SimpleBooleanProperty(false);

        //Bind togglePowerBtn style.
        this.togglePowerBtn.styleProperty().bind(Bindings.createStringBinding(()->{
            if(this.hotspotStarted.getValue()){
                return "-fx-border-radius: 100%;" +
                        "-fx-background-image: url(\"/images/power-off.png\");"+
                        "-fx-border-color: #FF0000; " +
                        "-fx-background-size: 40%;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-position: center;"+
                        "-fx-border-width: 1em;";
            }
            return "-fx-border-radius: 100%;" +
                    "-fx-background-image: url(\"/images/power-on.png\");"+
                    "-fx-background-size: 40%;" +
                    "-fx-background-repeat: no-repeat;" +
                    "-fx-background-position: center;"+
                    "-fx-border-width: 1em;"+
                    "-fx-border-color: #004000; " +
                    "-fx-border-width: 1em;";
        },this.hotspotStarted));


        //bind statusPane Event
        this.statusPane.textProperty().bind(Bindings.createStringBinding(()->{
            if(this.hotspotStarted.getValue()){
                return "Hospify is turned on.";
            }
            return "Hospify is turned off.";
        },this.hotspotStarted));
        this.statusPane.styleProperty().bind(Bindings.createStringBinding(()->{
            if(this.hotspotStarted.getValue()){
                return "-fx-text-fill: #004000;";
            }
            return "-fx-text-fill: #FFF;";
        }, this.hotspotStarted));

        //Position menu bar
        this.menuBar.setTranslateX( 87.0);

    }

    /**
     *
     * @param message
     */
    private void log(String message){
        try {
            this.logFile = new BufferedWriter(new FileWriter(new File(SplashScreenController.FILE_PATH+"log.log")));
            this.logFile.newLine();
            this.logFile.append("["+LocalDateTime.now().toString()+"]\t");
            this.logFile.append(message);
            this.logFile.newLine();
            this.logFile.flush();
            this.logFile.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @FXML
    private void closeStage(ActionEvent event) {
        this.controller.getStage().close();
        try {
            this.hotspot.stop();
        } catch (IOException e) {
            this.log(e.getLocalizedMessage()+"\n"+e.getCause());
            SwingUtilities.invokeLater(()->{
                this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                        e.getMessage(), TrayIconAdapter.ERROR);
            });
        }finally {
            System.exit(0);
        }

    }

    /**
     * This method hides the application window
     * and then display the icon on the window
     * tray.
     * @param event
     */
    @FXML
    private void hideStage(ActionEvent event){
        this.controller.getStage().hide();
    }


    /**
     * This method stops the hosted network
     */
    @FXML
    void toggleOnOff(ActionEvent event) {
        if(this.hotspot.getStatus().equalsIgnoreCase("started")){
            SwingUtilities.invokeLater(()->{
                try {
                    if(this.hotspot.stop()){
                        Platform.runLater(()->this.hotspotStarted.setValue(false));
                        this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                                "Hospify turned off...", TrayIconAdapter.INFO);

                        this.log("Hospify turned off...");
                    }else{
                        this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                                "Oops! unable to turn off hospify.", TrayIconAdapter.INFO);
                        this.log("Oops! unable to turn off hospify");
                    }
                } catch (IOException e1) {
                    this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                            e1.getLocalizedMessage(), TrayIconAdapter.ERROR);
                }
            });
         //Start the hotspot

        }else{
            SwingUtilities.invokeLater(()->{
                try {
                    if(this.hotspot.start()){
                        Platform.runLater(()->this.hotspotStarted.setValue(true));
                        this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                                "Hospify turned on", TrayIconAdapter.INFO);
                        this.log("Hospify turned on ...");
                    }else{
                        this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                                "Oops! unable to turn on hospify.", TrayIconAdapter.INFO);
                        this.log("Unable to turn on hospify");
                    }
                } catch (IOException e) {
                    this.trayIcon.addNofication("Hospify 2018 - Connection Status",
                            e.getLocalizedMessage(), TrayIconAdapter.ERROR);

                }
            });

        }
    }


    @FXML
    void HideMenu(MouseEvent event) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(.5),this.menuBar);
        FadeTransition fade = new FadeTransition(Duration.seconds(.5), this.menuCaption);
        fade.setFromValue(0.2);
        fade.setToValue(1);
        move.setToX(87.0);
        move.setFromX(-5.0);
        move.setInterpolator(Interpolator.EASE_OUT);
        move.playFromStart();
        fade.setInterpolator(Interpolator.LINEAR);
        fade.playFromStart();
        this.menuCaption.setVisible(true);

    }

    /**
     * This method shows the side menu
     * when mouse pointer enters the
     * menu area.
     * @param event
     */
    @FXML
    void showMenu(MouseEvent event) {
        TranslateTransition move = new TranslateTransition(Duration.seconds(.4),this.menuBar);
        FadeTransition fade = new FadeTransition(Duration.seconds(.5), this.menuCaption);
        move.setFromX(40.0);
        fade.setFromValue(1);
        fade.setToValue(0.2);
        move.setToX(-5.0);
        fade.setInterpolator(Interpolator.LINEAR);
        move.setInterpolator(Interpolator.EASE_OUT);
        move.playFromStart();
        fade.playFromStart();
        this.menuCaption.setVisible(false);
    }

    @FXML
    public void gotoHotspotSetting( ActionEvent event){
        this.controller.setScreen("hotspotConfig");
    }

    @FXML
    public void restartHotspot(ActionEvent event){
        SwingUtilities.invokeLater(()->{
            try {
                if(this.hotspot.getStatus().equalsIgnoreCase("started")) {
                    this.hotspot.stop();
                }
                if(this.hotspot.start()){
                    Platform.runLater(()->this.hotspotStarted.setValue(true));
                }else{
                    Platform.runLater(()->this.hotspotStarted.setValue(false));
                }

            } catch (IOException e) {
                this.trayIcon.addNofication("Hospify 2018 - Connection status",
                        e.getLocalizedMessage(),TrayIconAdapter.ERROR);
            }
        });
    }


}
