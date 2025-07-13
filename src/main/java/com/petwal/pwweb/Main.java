package com.petwal.pwweb;


import com.petwal.pwweb.server.WebServer;

public class Main {
    public static void main(String[] args) throws Exception {
        WebServer webServer = new WebServer();
        webServer.start(5000, "com.petwal.pwweb.controller");
    }
}