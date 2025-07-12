package com.petwal.pwweb;


import com.petwal.pwweb.server.WebServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello, World!");
        WebServer webServer = new WebServer();
        webServer.start(5000);
    }
}