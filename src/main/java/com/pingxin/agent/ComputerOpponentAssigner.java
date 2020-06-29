package com.pingxin.agent;

public class ComputerOpponentAssigner implements Runnable {

    private Object waitOnObject;

    public ComputerOpponentAssigner(Object waitOnObject) {
        this.waitOnObject = waitOnObject;
    }

    public void run() {
        try {
            Thread.sleep(60*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized(waitOnObject) {
            waitOnObject.notifyAll();
        }
    }
}
