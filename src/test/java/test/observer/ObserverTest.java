package test.observer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObserverTest {
    @Test
    void basicTest() {
        Subscriber sub = new Subscriber();
        Subscriber sub2 = new Subscriber();
        Sender send = new Sender();
        send.testSend();
        Subscriber sub3 = new Subscriber();
        send.testSend();
    }
}
