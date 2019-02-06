package com.sunday.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sunday.hotspot.Hotspot;
import com.sunday.hotspot.TrayIconAdapter;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This is a HotspotConfigController class
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 8/13/2018 @4:16 AM
 */
public class HotspotConfigController implements ControllerAdapter, Initializable {
    //Class properties goes here
    private ScreenController controller;
    private Hotspot hotspot;
    private String settingFile;
    private TrayIconAdapter trayIcon;

    @FXML
    private Label nameLabel;

    @FXML
    private JFXTextField hotspotName;

    @FXML
    private Label passwordLabel;

    @FXML
    private JFXPasswordField password;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private JFXButton closeBtn;

    @FXML
    private ImageView closeIcon;

    @FXML
    private ImageView hideIcon;

    @FXML
    private JFXButton hideBtn;


    /**
     * Constructor
     */
    public HotspotConfigController() {
        //Constructor logic goes here
       this.settingFile = SplashScreenController.FILE_PATH+"setting.ini";

    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        this.nameLabel.setLabelFor(this.hotspotName);
        this.closeBtn.setOnAction(e->{
            this.controller.getStage().close();
            System.exit(0);
        });

        this.saveBtn.disableProperty().bind(Bindings.createBooleanBinding(()->{
            if(this.hotspotName.textProperty().getValue().length() > 7 &&
                    this.password.textProperty().getValue().length() > 7){
                return false;
            }
            return true;
        },this.hotspotName.textProperty(),this.password.textProperty()));

        this.saveBtn.styleProperty().bind(Bindings.createStringBinding(()->{
            if(this.hotspotName.textProperty().getValue().length() > 7 &&
                    this.password.textProperty().getValue().length() > 7){
                return "-fx-background-color: #0097DF; -fx-text-fill: white";
            }
            return "-fx-background-color: gray;";
        },this.hotspotName.textProperty(), this.password.textProperty()));

    }

    /**
     * This method is used to set the
     * screen parent
     *
     * @param screenPage
     */
    @Override
    public void setScreenParent(ScreenController screenPage) {
        this.controller = screenPage;

    }

    /**
     * This method sets the display tray icon
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
        var prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(SplashScreenController.FILE_PATH+"setting.ini")));
            if(!prop.getProperty("ssid name").isEmpty()){
                this.hotspotName.setText(prop.getProperty("ssid name").replaceAll("\"",""));
            }
            if(!prop.getProperty("password").isEmpty()){
                this.password.setPromptText(prop.getProperty("password"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This method transit the scene
     * to hotspot service screen
     */
    @FXML
    private void saveConfig(ActionEvent event) {

        if(this.hotspotName.getText().length() > 7 && this.password.getText().length() > 7){
            SwingUtilities.invokeLater(()->{
                try {
                    var prop = new Properties();
                    prop.setProperty("mode", "allow");
                    prop.setProperty("ssid", this.hotspotName.getText());
                    prop.setProperty("password", this.password.getText());
                    this.hotspot.configure(prop);
                    prop = this.hotspot.getConfiguration();
                    prop.put("password",this.password.getText());
                    prop.store(new FileOutputStream(this.settingFile),
                                "Hospify 2018 Connection Settings");


                } catch (IOException e) {
                    this.trayIcon.addNofication("Hospify 2018 - Network configuration status",
                            e.getLocalizedMessage(), TrayIconAdapter.ERROR);
                }
                this.controller.setScreen("hotspotService");
            });
        }


    }

    /**
     * This method hides the application window
     * and then display the icon on the window
     * tray.
     * @param event this is the event triggered.
     */
    @FXML
    private void hideStage(ActionEvent event){
        this.controller.getStage().hide();
    }

    /**
     * this method loads and display the hotspot service
     * view.
     * @param event
     */
    @FXML
    void gotoMenu(ActionEvent event) {
        this.controller.setScreen("hotspotService");
    }

}
