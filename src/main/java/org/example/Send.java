package org.example;

import java.util.ArrayList;
import java.util.List;

public class Send {
    protected List<NewClient> newClients = new ArrayList<>();

    protected Send() {
    }

    protected void outSend(String msg) {
        for (NewClient client : newClients) {
            client.sendMsg(msg);
        }
    }
}