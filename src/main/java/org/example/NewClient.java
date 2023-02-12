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
    private String name = "null";
    private String inStr;

    protected Log log;

    protected NewClient(Socket socket, Send send) {
        try {
            this.log = new Log();
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.send = send;
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.recordLog();
    }

    @Override
    public String toString() {
        return "NewClient: " + socket.toString();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println(log.queueAdd(format.format(new Date()) + " new client: " + socket.getLocalAddress()));

            try {
                do {
                    out.println("Введите имя: max 20 size");
                    name = in.readLine();
                } while (name == null || name.trim().isEmpty() || name.length() > 20);
                Thread.currentThread().setName(name);
                log.queueAdd(format.format(new Date()) + " client connect: " + socket.getInetAddress() + " " + name);

            } catch (SocketException e) {
                log.stop();
                System.out.println(log.queueAdd(format.format(new Date()) + " client close: " + socket.getInetAddress() + " " + name));
                send.newClients.remove(this);
                return;
            }

            out.println(format.format(new Date()) + " Hello " + name);
            boolean startServer = true;
            while (startServer) {
                try {
                    inStr = in.readLine();
                    if (!(inStr != null && !inStr.trim().isEmpty())) {
                        continue;
                    }

                    if (inStr.equals("exit")) {
                        send.newClients.remove(this);
                        send.outSend(inStr, name);
                        log.stop();
                        System.out.println(log.queueAdd(format.format(new Date()) + " client close: " + socket.getInetAddress() + " " + name));
                        break;
                    }
                    send.outSend(inStr, name);
                    System.out.println(log.queueAdd(format.format(new Date()) + " " + name + ": " + inStr));
                } catch (SocketException e) {
                    send.outSend(inStr, name);
                    send.newClients.remove(this);
                    log.stop();
                    System.out.println(log.queueAdd(format.format(new Date()) + " client close: " + socket.getInetAddress() + " " + name));
                    startServer = false;
                }
            }
        } catch (IOException | InterruptedException e) {
            send.newClients.remove(this);
            e.printStackTrace();
        }
    }

    protected void sendMsg(String inStr, String name) {
        out.printf("%s %s: %s \n", format.format(new Date()), name, inStr);
    }
}