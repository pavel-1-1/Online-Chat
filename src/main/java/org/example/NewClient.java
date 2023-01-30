package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewClient implements Runnable {

    private final SimpleDateFormat format = new SimpleDateFormat("d.M.yy HH.mm");
    private Socket socket;
    private PrintWriter out;
    private Send send;
    private String name;

    protected NewClient(Socket socket, Send send) {
        try {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.send = send;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "NewClient: " + socket.toString();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("new client: " + socket.getPort() + socket.getLocalAddress());
            out.println("Введите имя: ");
            try {
                while (true) {
                    name = in.readLine();
                    if (name != null) {
                        break;
                    } else {
                        out.println("Введите имя: ");
                    }
                }
            } catch (SocketException e) {
                System.out.println("клиент отключился: " + socket.getInetAddress());
                send.newClients.remove(this);
                return;
            }

            out.println("Hello " + name);
            boolean startServer = true;
            String inStr;
            while (startServer) {
                try {
                    inStr = in.readLine();
                    assert inStr != null;
                    if (inStr.equals("end")) {
                        send.newClients.remove(this);
                        send.outSend(inStr, name);
                        System.out.println("client close: " + socket.getInetAddress() + " " + name);
                        break;
                    }
                    send.outSend(inStr, name);
                    System.out.println(inStr);
                } catch (SocketException e) {
                    send.newClients.remove(this);
                    System.out.println("client close: " + socket.getInetAddress() + " " + name);
                    startServer = false;
                }
            }
        } catch (IOException e) {
            send.newClients.remove(this);
            e.printStackTrace();
        }
    }

    protected void sendMsg(String inStr, String name) {
        out.printf("%s %s: %s \n", format.format(new Date()), name, inStr);
    }
}