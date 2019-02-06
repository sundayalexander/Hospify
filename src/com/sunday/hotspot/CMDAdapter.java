package com.sunday.hotspot;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * This is a command prompt class.
 * This class is used to work with window
 * related command via the net share interface.
 * @author Amowe Sunday Alexander
 */

public class CMDAdapter implements Terminal{
    private Runtime runtime;
    private Process process;


    /**
     * Public constructor for the Command prompt class.
     * This constructor initialize the command prompt environment.
     * @throws IOException this is thrown if the method could not perform
     * IO operation on the file.
     */
    public CMDAdapter(){
        this.runtime = Runtime.getRuntime();
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
    public boolean startNetwork() throws IOException {
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
            throw new InterruptedIOException(read);
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
    public boolean stopNetwork() throws IOException {
        this.process = this.runtime.exec("net stop sharedaccess");
        this.process.destroy();
        this.process = this.runtime.exec("netsh wlan stop hostednetwork");
        String read;
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
        if((read = reader.readLine().trim()) != null){
            //Get the response message
            if(read.equalsIgnoreCase("The hosted network stopped.")){
                reader.close();
                this.process.destroy();
                return true;
            }else{
                throw new InterruptedIOException(read);
            }
        }else{
            return false;
        }

    }

    /**
     * This method checks the hotspot status
     * @return if the method was able to the connection
     * status it returns Properties object, otherwise
     * null is return.
     */
    @Override
    public Properties getConfiguration(){
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

        }
        return prop;
    }

    /**
     * This set the hostednetwork properties such as the
     * mode, ssid, and key
     * @throws IOException This is thrown if the method
     * could not perform the required read operation
     * from the hotspot.
     */
    @Override
    public void setConfiguration(Properties properties) throws IOException {
        BufferedReader reader = this.execute("netsh wlan set hostednetwork mode="+properties.getProperty("mode"));
        String read = reader.readLine().trim();
        if(read.endsWith("The hosted network mode has been set to allow.")){
            this.killProcess();
            reader = this.execute("netsh wlan set hostednetwork key="+properties.getProperty("password"));
            read = reader.readLine().trim();
            System.out.println(reader.readLine());
            if (read.endsWith("The user key passphrase of the hosted network has been successfully changed.")){
                this.killProcess();
                reader = this.execute("netsh wlan set hostednetwork ssid="+properties.getProperty("ssid"));
                read = reader.readLine().trim();
                if (read.endsWith("The SSID of the hosted network has been successfully changed.")){
                    this.killProcess();
                }else{
                    throw new InterruptedIOException(read);
                }
            }else{
                throw new InterruptedIOException(read);
            }
        }else {
            throw new InterruptedIOException(read);
        }

    }
    

    /**
     * @throws IOException
     */
    @Override
    public void setConfiguration(String key, String value) throws IOException {
        BufferedReader reader;
        if(key.equalsIgnoreCase("password")){
            reader = this.execute("netsh wlan set hostednetwork key="+value);
        }else{
            reader = this.execute("netsh wlan set hostednetwork "+key+"="+value);
        }
        String read = reader.readLine().trim();
        if (read.endsWith("The user key passphrase of the hosted network has been successfully changed.") ||
                read.endsWith("The user key passphrase of the hosted network has been successfully changed.") ||
                read.endsWith("The SSID of the hosted network has been successfully changed.")){
            this.killProcess();
        }else{
            throw new InterruptedIOException(read);
        }
    }

    /**
     *
     * @return
     * @throws IOException
     */
    @Override
    public ArrayList<String> getNetworks() throws IOException {
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

    /**
     *
     * @return InputStream
     */
    public InputStream getLog(){
        return this.process.getInputStream();
    }


}
