package com.ryx.epubtest;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URLConnection;

public class AppConstants {
    public static final String LOCALHOST = "http://127.0.0.1";
    public static final int DEFAULT_PORT_NUMBER = 8080;
    public static final String STREAMER_URL_TEMPLATE = "%s:%d/%s/";
    public static final String DEFAULT_STREAMER_URL = LOCALHOST + ":" + DEFAULT_PORT_NUMBER + "/";

    public static int getAvailablePortNumber(int port) throws IOException {
        ServerSocket serverSocket = null;
        int portNumAvailable;

        try {
            serverSocket = new ServerSocket(port);
            portNumAvailable = port;
        } catch (Exception e) {
            serverSocket = new ServerSocket(0);
            portNumAvailable = serverSocket.getLocalPort();
        } finally {
            if (serverSocket != null)
                serverSocket.close();
        }

        return portNumAvailable;
    }
}
