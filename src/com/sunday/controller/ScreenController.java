package com.sunday.controller;


import com.sunday.hotspot.TrayIconAdapter;
import com.sunday.hotspot.Hotspot;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.util.HashMap;

/**
 * This is a ScreenController class
 *
 * @author: Amowe Sunday Alexander
 * @version: 1.2
 * @date: 8/13/2018 @4:52 AM
 */
public class ScreenController extends StackPane {
    //Class properties goes here
    //This property holds the screen to be controlled.
    private HashMap<String, Node> screens = new HashMap<>();
    //This property holds the stage
    private Stage stage;

    /**
     * This is a custom variable that holds
     * the hotspot object.
     */
    private Hotspot hotspot;
    private TrayIconAdapter trayIcon;


    /**
     * Constructor
     */
    public ScreenController() {
        //Constructor logic goes here
        super();
    }

    /**
     * This method adds the specified screen
     * to the collection.
     * @param name this is the name of the
     *             node to be added.
     * @param screen this is the actual node
     *               to be added to the
     *               collection.
     */
    private void addScreen(String name, Node screen){
        this.screens.put(name,screen);

    }

    /**
     * This method is used to get
     * a specified node from the
     * collection.
     * @param name this is the name
     *             of the node in the
     *             collection to be
     *             returned.
     * @return Node this return
     * a node object
     */
    public Node getScreen(String name){
        return this.screens.get(name);
    }

    /**
     * This method loads the specified
     * node to screen
     * @param name this is the id that will
     *             be used to represent the
     *             fxm file to be loaded
     * @param resource this is the resource
     *                 path for the fxml file
     *                 to be loaded
     * @return this is set to true if the file
     * is successfully loaded otherwise false.
     */
    public boolean loadScreen(String name, String resource){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        try {
            Parent loadScreen = fxmlLoader.load();
            ControllerAdapter controller = fxmlLoader.getController();
            controller.setScreenParent(this);
            controller.setHotspot(this.hotspot);
            controller.setDisplayTrayIcon(this.trayIcon);
            this.addScreen(name, loadScreen);
            return true;
        } catch (IOException e) {
            return false;

        }
    }

    /**
     * This method set the given screen
     * on the scene graph.
     * @param name this is the id of
     *             the screen or node
     *             to be set.
     * @return thi  is set to true if
     * the screen is loaded successfully
     * otherwise false.
     */
    public boolean setScreen(final String name){
        if(this.screens.get(name) != null){
            final DoubleProperty opacity = this.opacityProperty();

            //if there exist more than one screen
            if (!this.getChildren().isEmpty()){
                Timeline fade = new Timeline(
                        new KeyFrame(Duration.ZERO,new KeyValue(opacity,1.0)),
                        new KeyFrame(new Duration(1000), event ->{
                            getChildren().remove(0);
                            getChildren().add(0,this.screens.get(name));
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity,0.2)),
                                    new KeyFrame(new Duration(800),new KeyValue(opacity,1.0))
                            );
                            fadeIn.play();
                        },new KeyValue(opacity, 0.2))
                );
                fade.play();
            }else {
                this.setOpacity(0.2);
                getChildren().add(this.screens.get(name));
                Timeline fadeIn = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.2)),
                        new KeyFrame(new Duration(2500), new KeyValue(opacity, 1.0))
                );
                fadeIn.play();
            }
            return true;
        }else{
            return false;
        }

    }

    /**
     * This method will remove the
     * screen with the given name
     * from the collection of screens.
     * @param name this is id of the
     *             screen to be removed.
     * @return this is set to true if
     * the given screen is removed successfully
     * otherwise false.
     */
    public boolean unloadScreen(String name){
        return this.screens.remove(name) != null;
    }

    /**
     * This method sets the primary
     * stage of the app.
     * @param stage This is the
     *              stage to be
     *              set
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /**
     * This returns the primary stage
     * of the application.
     * @return Stage
     */
    public Stage getStage(){
        return this.stage;
    }


    /**
     *
     * @param hotspot
     */
    public void setHotspot(Hotspot hotspot){
        this.hotspot = hotspot;
    }

    /**
     *
     * @param trayIcon
     */
    public void setTrayIcon(TrayIconAdapter trayIcon){
        this.trayIcon = trayIcon;
    }

}
