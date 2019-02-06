package com.sunday.hotspot;

import java.io.IOException;
import java.util.Properties;

/**
 * This is a Hotspot class
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 1/27/2019 @4:24 PM
 */
public class Hotspot {
    //Class properties goes here
    protected Terminal terminal;

    /**
     * Constructor
     */
    public Hotspot() {
        //Constructor logic goes here
        this.terminal = new CMDAdapter();
    }

    /**
     *
     * @return boolean
     * @throws IOException
     */
    public boolean start() throws IOException {
        return this.terminal.startNetwork();
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public boolean stop() throws IOException {
        return  this.terminal.stopNetwork();
    }

    /**
     *
     * @param name
     * @throws IOException
     */
    public void setSSID(String name) throws IOException {
        this.terminal.setConfiguration("ssid",name);
    }

    /**
     *
     * @param password
     * @throws IOException
     */
    public void setPassword(String password) throws IOException {
        this.terminal.setConfiguration("password",password);
    }

    /**
     *
     * @param mode
     * @throws IOException
     */
    public void setMode(String mode) throws IOException {
        this.terminal.setConfiguration("mode", mode);
    }

    /**
     *
     * @return
     */
    public Properties getConfiguration(){
        return this.terminal.getConfiguration();
    }

    /**
     *
     * @return
     */
    public String getSSID(){
        return this.terminal.getConfiguration().getProperty("ssid");
    }

    /**
     *
     * @return
     */
    public String getMode(){
        return this.terminal.getConfiguration().getProperty("mode");
    }

    /**
     *
     * @return
     */
    public Terminal getTerminal(){
        return  this.terminal;
    }

    /**
     *
     * @return
     */
    public String getStatus(){
        return this.terminal.getConfiguration().getProperty("status");
    }

    public void configure(Properties properties) throws IOException {
        this.terminal.setConfiguration(properties);
    }
}
