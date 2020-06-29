package com.pingxin.agent;

import com.pingxin.model.Dudoku;
import com.pingxin.service.DudokuService;

import java.util.concurrent.ConcurrentHashMap;

public class CurrentDudokuRemover implements Runnable {

    private ConcurrentHashMap<String, Dudoku> currentGames;
    private DudokuService dudokuService;

    public CurrentDudokuRemover(ConcurrentHashMap<String, Dudoku> currentGames, DudokuService dudokuService) {
        this.currentGames = currentGames;
        this.dudokuService = dudokuService;
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(25*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (String email : currentGames.keySet()) {
                Dudoku game = currentGames.get(email);
                if (game != null && game.isExpired()) {
                    dudokuService.endGame(game, "timeout", email);
                }
            }
        }
    }
}
