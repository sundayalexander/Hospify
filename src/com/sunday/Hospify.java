/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sunday;

import com.sunday.controller.ScreenController;
import com.sunday.controller.SplashScreenController;
import com.sunday.hotspot.Hotspot;
import com.sunday.hotspot.TrayIconAdapter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Enumeration;


/**
 *
 * @author Amowe Sunday Alexander
 * @version 1.0
 */
public class Hospify extends Application {
    private double startX, startY;
    private BufferedWriter logFile;
    private Hotspot hotspot;
    private TrayIconAdapter trayIcon;
    private Stage primaryStage;
    private SimpleBooleanProperty show;
    private SimpleBooleanProperty disp;




    public static void main(String[] args) {
        launch(args);
    }

    public Hospify(){
        this.hotspot = new Hotspot();
        this.show = new SimpleBooleanProperty(true);
        this.disp = new SimpleBooleanProperty(false);
        try {
            var image = ImageIO.read(this.getClass().getResource("/images/logo.png"));
            this.trayIcon = new TrayIconAdapter(image,"Hospify 2018");

            //handle open menu
            this.trayIcon.addMenu("Open",e -> {
                this.show.setValue(true);
            });

            //Handle Hide menu
            this.trayIcon.addMenu("Hide", e -> {
                this.show.setValue(false);
                Platform.runLater(()->{
                    this.primaryStage.hide();

                });
            });

            //Handle exit menu
            this.trayIcon.addMenu("Exit",e->{
                this.trayIcon.close();
                Platform.runLater(()-> {
                    primaryStage.close();
                });
            });
            if(!this.trayIcon.isDisplayed()){
                try {
                    this.trayIcon.display();
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            try {
                var logFile = new BufferedWriter(new FileWriter(new File(SplashScreenController.FILE_PATH+"log.log")));
                var date = LocalDateTime.now();
                logFile.append(date.toString());
                logFile.append(e.getMessage());
                logFile.newLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        ScreenController mainContainer = new ScreenController();
        mainContainer.setHotspot(this.hotspot);
        mainContainer.setTrayIcon(this.trayIcon);
        mainContainer.loadScreen("splashScreen","/res/layout/splashScreen.fxml");
        mainContainer.loadScreen("connectedDevice","/res/layout/connectedDevice.fxml");
        mainContainer.loadScreen("hotspotConfig","/res/layout/hotspotConfig.fxml");
        mainContainer.loadScreen("hotspotService","/res/layout/hotspotService.fxml");
        mainContainer.setScreen("splashScreen");


        //Add Frame Icon
        Image logo = new Image("/res/images/logo.png");
        primaryStage.getIcons().add(logo);

        //Setup the scene graph and the stage
        Group root = new Group();
        root.getChildren().addAll(mainContainer);
        Scene scene = new Scene(root,711,489);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        root.setOnMousePressed((e)->{
            startX = e.getSceneX() - primaryStage.getX();
            startY = e.getSceneY() - primaryStage.getY();
        });
        root.setOnMouseDragged((e)->{
            primaryStage.setX(e.getSceneX()-startX);
            primaryStage.setY(e.getSceneY()-startY);
        });


        primaryStage.show();
        mainContainer.setStage(primaryStage);

        //Bind primarystage to show event trigger.


        System.out.println(this.primaryStage.showingProperty().getValue());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(this.hotspot.getStatus().equalsIgnoreCase("started")){
            this.hotspot.stop();
        }
    }
}
