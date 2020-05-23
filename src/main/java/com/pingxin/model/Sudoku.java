package com.pingxin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sudoku {

    public static final int expirationTime = 20*60*1000;
    private List<String> players;
    private int[][] board;
    private int[][] solution;
    private int[][] origin;
    private int mistakes;
    private Date createdTime;

    public Sudoku(String player, String board, String solution) {
        this.players = new ArrayList<>();
        this.players.add(player);
        this.board = new int[9][9];
        this.solution = new int[9][9];
        this.origin = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++){
                int index = i * 9 + j;
                this.board[i][j] = Character.getNumericValue(board.charAt(index));
                if (this.board[i][j] != 0) origin[i][j] = 1;
                this.solution[i][j] = Character.getNumericValue(solution.charAt(index));
            }
        }
        this.createdTime = new Date();
    }

    public synchronized int fillCell(int x, int y, int value) {
        if (this.board[x][y] != 0) return -1;
        if (this.solution[x][y] != value) {
            this.mistakes += 1;
            return this.mistakes;
        }
        this.board[x][y] = value;
        this.origin[x][y] = 2;
        return 0;
    }

    public boolean isExpired() {
        Date now = new Date();
        return now.getTime()-createdTime.getTime() > expirationTime;
    }

    public int getTimeExpired() {
        Date now = new Date();
        return (int)((now.getTime()-createdTime.getTime()) / 1000);
    }

    public static int getExpirationTime() {
        return expirationTime;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int[][] getSolution() {
        return solution;
    }

    public void setSolution(int[][] solution) {
        this.solution = solution;
    }

    public int[][] getOrigin() {
        return origin;
    }

    public void setOrigin(int[][] origin) {
        this.origin = origin;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
