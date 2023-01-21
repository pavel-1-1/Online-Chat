package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Send send = new Send();

        System.out.println("server started");
        int port = 8080;
        Socket socket;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                send.newClients.forEach(System.out::println);
                System.out.println(send.newClients.size());

                socket = serverSocket.accept();
                NewClient newClient = new NewClient(socket, send);
                new Thread(newClient).start();
                send.newClients.add(newClient);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
