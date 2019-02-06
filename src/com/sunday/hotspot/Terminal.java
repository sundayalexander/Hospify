package com.sunday.hotspot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This is a Terminal Interface
 *
 * @author: Amowe Sunday Alexander
 * @version:
 * @date: 1/27/2019 @2:40 PM
 */
 public interface Terminal {
    //Class properties goes here
    boolean startNetwork() throws IOException;
    boolean stopNetwork() throws IOException;
    Properties getConfiguration();
    void setConfiguration(Properties properties) throws IOException;
    void setConfiguration(String key, String value) throws IOException;
    ArrayList<String> getNetworks() throws IOException;
    InputStream getLog();
}
