package com.petwal.pwweb.server;


import com.petwal.pwweb.model.HttpRequest;
import com.petwal.pwweb.parser.HttpRequestParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public void start(final int port) throws IOException {

        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final Socket socket = serverSocket.accept();
                     final BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     final BufferedWriter outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                    final HttpRequest request = HttpRequestParser.parse(inputStream);

                    System.out.println(request);

                    outputStream.write("Response.");
                    outputStream.flush();
                }
            }

        }

    }

}
