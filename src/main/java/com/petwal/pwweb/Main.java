package com.petwal.pwweb;


import com.petwal.pwweb.core.Dispatcher;
import com.petwal.pwweb.core.Server;

public class Main {
    public static void main(String[] args) {
        final Dispatcher dispatcher = new Dispatcher("com.petwal.pwweb.controller");
        final Server server = new Server(5000, dispatcher);
        server.start();
    }
}