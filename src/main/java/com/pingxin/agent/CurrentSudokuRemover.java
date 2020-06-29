package com.pingxin.agent;

import com.pingxin.model.Sudoku;
import com.pingxin.service.SudokuService;

import java.util.concurrent.ConcurrentHashMap;

public class CurrentSudokuRemover implements Runnable {

    private ConcurrentHashMap<String, Sudoku> currentGames;
    private SudokuService sudokuService;

    public CurrentSudokuRemover(ConcurrentHashMap<String, Sudoku> currentGames, SudokuService sudokuService) {
        this.currentGames = currentGames;
        this.sudokuService = sudokuService;
    }

    public void run() {
        System.out.println("SudokuRemover running");

        while (true) {
            try {
                Thread.sleep(25*60*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (String email : currentGames.keySet()) {
                Sudoku game = currentGames.get(email);
                if (game != null && game.isExpired()) {
                    System.out.println("removing game");
                    System.out.println(game.getPlayers());
                    System.out.println(game.getTimeExpired());
                    sudokuService.endGame(game, "timeout", email);
                }
            }
        }
    }
}
