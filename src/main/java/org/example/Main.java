package org.example;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Send send = new Send();

        System.out.println("server started");
        int port = parseConfig();

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

    private static int parseConfig() {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            Object obj = parser.parse(new FileReader("config.json"));
            jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return Integer.parseInt((String) jsonObject.get("port"));
    }
}
