package com.pingxin.agent;

import com.pingxin.model.Dudoku;

import java.util.concurrent.ConcurrentHashMap;

public class MatchedDudokuDelayedRemover implements Runnable {

    private ConcurrentHashMap<String, Dudoku> gameLookUp;
    private String email;

    public MatchedDudokuDelayedRemover(String email, ConcurrentHashMap<String, Dudoku> gameLookUp) {
        this.email = email;
        this.gameLookUp = gameLookUp;
    }

    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gameLookUp.remove(email);
    }
}
