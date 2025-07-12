package com.petwal.pwweb;


import com.petwal.pwweb.server.WebServer;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        WebServer webServer = new WebServer();
        webServer.registerRoutes("com.petwal.pwweb.controller");
        webServer.start(5000);
    }
}