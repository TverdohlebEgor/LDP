package test.observer;

import behavioral.observer.NotificationHandler;

public class Sender {
    Sender() {
        NotificationHandler.subscribe("ChannelTest", this);
    }

    public void testSend() {
        NotificationHandler.send("ChannelTest", "printTest", "THIS IS WHAT I'M PRINTING", 69);
    }
}
