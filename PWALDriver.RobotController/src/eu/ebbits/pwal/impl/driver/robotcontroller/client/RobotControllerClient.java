package eu.ebbits.pwal.impl.driver.robotcontroller.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * Implementation of the robot controller client used to control the robot controller.
 * 
 * Copyright (c) 2010-2013 the ebbits project. All Rights Reserved.
 *
 * @author    ISMB
 * @version    %I%, %G%
 * @since PWAL 1.0
 *
 */
public class RobotControllerClient {

    private Logger log = Logger.getLogger(this.getClass().getName());
    
    private static final String QUIT_COMMAND = "Quit";
    private static final int SOCKET_TIMEOUT = 1000;
    private static final int DEFAULT_PORT_NUMBER = 9000;
    
    private Socket client = null;
    private String host = "129.100.1.57";
    private int port = DEFAULT_PORT_NUMBER;
    private BufferedReader controllerIn = null;
    private PrintWriter controllerOut = null;

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Connects to the robot
     * 
     */
    public void openConnection() {
        try {
            client = new Socket(host, port);    
            log.debug("Trying " + InetAddress.getByName(host).getHostAddress() + ":" + port + "...");
            
            if (client.isConnected() && !client.isClosed()) {
                client.setSoTimeout(SOCKET_TIMEOUT);
                client.setTcpNoDelay(false);
    
                log.debug("Connected to " + client.getRemoteSocketAddress() + ".");
                controllerIn =
                        new BufferedReader(new InputStreamReader(client.getInputStream()));
                    
                while ((controllerIn != null) && (controllerIn.ready())) {
                    //LOG.debug(serverInput.readLine());
                    controllerIn.readLine();
                }
    
                controllerOut =
                        new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
            }
        } catch (UnknownHostException ex){
            log.error("Error opening the connection, unknown host ",ex);
        } catch (IOException e) {
            log.error("Error opening the connection",e);
        }
    }

    /**
     * Sends a command to the robot controller
     * 
     * @param command - command t send as <code>String</code>
     * 
     * @return    the response as <code>String</code>
     * 
     * @throws IOException - if something goes wrong sending the command
     */
    public synchronized String sendRobotControllerCommand(String command)
            throws IOException {
        if ( (command == null) || (command.isEmpty()) || (command.equalsIgnoreCase("quit")) || 
                (client == null) || (!client.isConnected()) || (client.isClosed()) ||
                (controllerIn == null) || (controllerOut == null) ) {
            return null;
        }
        log.debug("Client: " + command);
        controllerOut.println(command);

        String response = "";
        do {
            response = response + " " + controllerIn.readLine();
        } while (controllerIn.ready());

        log.debug("Server: " + response);
        return response.trim();
    }

    /**
     * Method used to close the connection to the robot
     * 
     */
    public void closeConnection() {
        try {
            if (controllerOut != null) {
                controllerOut.println(QUIT_COMMAND);
                controllerOut.close();
            }
            if (controllerIn != null) {
                controllerIn.close();
            }
            if (client != null) {
                client.close();
            }
        } catch(IOException e) {
            log.error("Error closing the connection",e);
        }
    }
}
