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
        return "NewClient{" +
                "socket=" + socket +
                '}';
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("new client: " + socket.getPort() + socket.getLocalAddress());
            out.println("start go: ");
            boolean startServer = true;
            String inStr;
            while (startServer) {
                try {
                    inStr = in.readLine();
                    assert inStr != null;
                    if (inStr.equals("end")) {
                        send.newClients.remove(this);
                        send.outSend(inStr);
                        System.out.println("client close: " + socket.getPort() + inStr);
                        break;
                    }
                    send.outSend(inStr);
                    System.out.println(inStr);
                } catch (SocketException e) {
                    send.newClients.remove(this);
                    System.out.println("client close: " + socket.getPort());
                    startServer = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void sendMsg(String inStr) {
        out.printf("%s %s \n", format.format(new Date()), inStr);
    }
}