package com.lexosoft.Windows;

import jdk.management.resource.ResourceRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * This is a Windows command prompt class.
 * This class is used to work with window
 * related command via the net share interface.
 * @author Amowe Sunday Alexander
 */

public class CMD {
    private Properties environs;
    private Runtime runtime;
    private File initFile;
    private Process process;
    private String setError;


    /**
     * Public constructor for the Command prompt class.
     * This constructor initialize the command prompt environment.
     * @throws IOException this is thrown if the method could not perform
     * IO operation on the file.
     */
    public CMD() throws IOException{
        this.runtime = Runtime.getRuntime();
        String path = System.getenv("LOCALAPPDATA") + "/Hospify";
        this.initFile = new File(path);
        if(!this.initFile.exists()){
            this.initFile.mkdir();
            path = path + "/settings.ini";
            this.initFile = new File(path);
            this.initFile.createNewFile();
        }else{
            path = path + "/settings.ini";
            this.initFile = new File(path);
            if(!this.initFile.exists()){
                this.initFile.createNewFile();
            }
        }
        this.environs = new Properties();
        this.environs.load(new FileInputStream(this.initFile));
    }

    /**
     * This method execute the netsh command
     * @param command The command to be executed.
     * @return  A bufferedreader object which holds
     *               the response from the process.
     * @throws IOException This is thrown if the method
     * get and appropriate response from the process Object.
     */
    private BufferedReader execute(String command) throws IOException {
       this.process = this.runtime.exec(command);
       return new BufferedReader(new InputStreamReader(this.process.getInputStream()));
    }

    /**
     * This method
     * @return This is set as true if the
     * method was able to destroy the process,
     * otherwise it is set to false.
     */
    private Boolean killProcess(){
        this.process.destroy();
        if(this.process.isAlive()){
            return false;
        }else{
            return true;
        }
    }

    /**
     * This method starts the windows hostednetwork for environment
     * setup use the corresponding setter methods.
     * @return this is set to true if the network could be
     * started, otherwise false.
     * @throws IOException This is thrown if the method
     * could not perform IO operation.
     */
    public Boolean startHostednetwork() throws IOException {
        this.process = this.runtime.exec("net start sharedaccess");
        this.process.destroy();
        this.process = this.runtime.exec("netsh wlan start hostednetwork");
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
        String read = reader.readLine().trim();
        //Get the response message
        if(read.equalsIgnoreCase("The hosted network started.")){
            reader.close();
            this.process.destroy();
            return true;
        }else{
            this.setError = reader.readLine().trim();
            reader.close();
            this.process.destroy();
            return false;
        }
    }

    /**
     * This method stops the hostednetwork.
     * @return This is set to true if the method could the
     * running hosted network, otherwise false.
     * @throws IOException This is thrown if the method could
     * not perform read operation on the returned stream
     * by the process.
     */
    public Boolean stopHostednetwork() throws IOException {
        this.process = this.runtime.exec("net stop sharedaccess");
        this.process.destroy();
        this.process = this.runtime.exec("netsh wlan stop hostednetwork");
        String read;
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
        read = reader.readLine().trim();
        //Get the response message
            if(read.equalsIgnoreCase("The hosted network stopped.")){
                reader.close();
                this.process.destroy();
                return true;
            }else{
                this.setError = read;
                reader.close();
                this.process.destroy();
                return false;
            }
    }

    /**
     * This method checks the hotspot status
     * @return if the method was able to the connection
     * status it returns Properties object, otherwise
     * null is return.
     */
    public Properties getProperties(){
        Properties prop = null;
        try {
            this.process = this.runtime.exec("netsh wlan show hostednetwork");
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            prop = new Properties();
            String read;
            while((read = reader.readLine()) != null){
                read = read.trim();
                if(read.contains(":")){
                    String key = read.split(":")[0];
                    String value = read.split(":")[1];
                    prop.put(key.trim().toLowerCase(),value.trim());
                }

            }
            reader.close();
            this.process.destroy();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return prop;
    }

    /**
     * This set the hostednetwork properties such as the
     * mode, ssid, and key
     * @param mode The mode of the ssid which could assume
     *             one of the following values: allow, disallow.
     * @param ssid The Name of the network which will be
     *             used to connect and identify the network.
     * @param password The password or key of the hostednetwork.
     * @return This is set to true if all the parameters are
     *          successfully configured, otherwise false.
     * @throws IOException This is thrown if the method
     * could not perform the required read operation
     * from the terminal.
     */

    public Boolean setHostednetwork(String mode, String ssid, String password) throws IOException {
        BufferedReader reader = this.execute("netsh wlan set hostednetwork mode="+mode);
        String read = reader.readLine().trim();
        if(read.endsWith("The hosted network mode has been set to allow.")){
            this.killProcess();
            reader = this.execute("netsh wlan set hostednetwork key="+password);
            read = reader.readLine().trim();
            System.out.println(reader.readLine());
            if (read.endsWith("The user key passphrase of the hosted network has been successfully changed.")){
                this.killProcess();
                reader = this.execute("netsh wlan set hostednetwork ssid="+ssid);
                read = reader.readLine().trim();
                if (read.endsWith("The SSID of the hosted network has been successfully changed.")){
                    this.killProcess();
                }else{
                    this.setError = read;
                    return false;
                }
            }else{
                setError = read;
                return false;
            }
        }else {
            this.setError = read;
            return false;
        }
        return true;
    }

    public Boolean setHostednetwork(String key, String value) throws IOException {
        BufferedReader reader = this.execute("netsh wlan set hostednetwork "+key+"="+value);
        String read = reader.readLine().trim();
        if (read.endsWith("The user key passphrase of the hosted network has been successfully changed.") ||
                read.endsWith("The user key passphrase of the hosted network has been successfully changed.") ||
                read.endsWith("The SSID of the hosted network has been successfully changed.")){
            this.killProcess();
            return true;
        }else{
            this.setError = read;
            return false;
        }
    }

    /**
     * This returns the hostednetwork error message.
     * @return The Occured error.
     */
    public String getError(){
        return this.setError;
    }

    /**
     * This method returns the properties of the previously
     * hostednetwork settings.
     * @return Properties of the hostednetwork
     */

    public Properties getEnvirons(){
        return this.environs;
    }

    /**
     * This method returns the Initialization file
     * for necessary File IO operations.
     * @return File object.
     */
    public File getInitFile(){
        return this.initFile;
    }

    /**
     * This method returns the list of adapters and it
     * connection properties.
     * @return This is the list of adapters and with its
     * various properties.
     * @throws IOException This is thrown if the method
     * could not get response from the terminal
     */

    public ArrayList<String> getAdapters() throws IOException {
        BufferedReader reader = this.execute("netsh interface show interface");
        StringTokenizer tokens;
        String read;
        StringBuilder buffer;
        ArrayList<String> adapters = new ArrayList<>();
        reader.readLine();reader.readLine();
        while((read = reader.readLine()) != null){
            if(!read.startsWith("--") && !read.isEmpty()){
                tokens = new StringTokenizer(read," ");
                Stack<String> reverse = new Stack<>();
                buffer = new StringBuilder();
                while(tokens.hasMoreTokens()){
                    reverse.push(tokens.nextToken().trim().concat(" "));
                }
                while(!reverse.empty()){
                   buffer.append(reverse.pop());
                }

                if(!buffer.toString().equalsIgnoreCase(" ")) {
                    adapters.add(buffer.toString());
                }
            }
        }
        this.killProcess();

        //Get Broadband interface
        reader = this.execute("netsh mbn show interfaces");
        buffer = new StringBuilder();
        while((read = reader.readLine()) != null){
            if (read.trim().toLowerCase().startsWith("guid")) {
                buffer.append(read.split(":")[1].trim().replace(" ","-")+" ");
            }else if(read.trim().toLowerCase().startsWith("description")){
                buffer.append(read.split(":")[1].trim().replace(" ","-")+" ");
            }else if (read.trim().toLowerCase().startsWith("state")){
                buffer.append(read.split(":")[1].trim().replace(" ","-")+" ");
            }else if(read.trim().toLowerCase().startsWith("signal")){
                buffer.append(read.split(":")[1].trim().replace("","")+" ");
            }
        }
        if (!buffer.toString().isEmpty()){
            adapters.add(buffer.toString());
        }
        this.killProcess();
        return adapters;
    }
}
