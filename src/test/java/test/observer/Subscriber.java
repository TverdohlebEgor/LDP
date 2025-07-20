package test.observer;

import behavioral.observer.NotificationHandler;

public class Subscriber {
    Subscriber(){
        NotificationHandler.subscribe("ChannelTest",this);
    }

    public void printTest(String string, int inti){
        System.out.println(this.toString()+": "+string+" : "+inti);
    }
}
