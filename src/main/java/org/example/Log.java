package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Log {
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(100);

    private boolean running = true;

    protected Log() {
    }

    protected String queueAdd(String text) throws InterruptedException {
        queue.put(text);
        return text;
    }

    protected void recordLog() {
        Runnable task = () -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("file.log", true))) {
                while (running) {
                    writer.write(queue.take());
                    writer.append('\n');
                    writer.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException ignored) {

            }
        };
        new Thread(task).start();
    }

    protected void stop() {
        running = false;
    }
}
