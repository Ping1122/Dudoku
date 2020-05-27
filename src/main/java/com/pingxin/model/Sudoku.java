package com.pingxin.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sudoku {

    private static final int expirationTime = 20*60*1000;
    private List<String> players;
    private int[][] board;
    private int[][] solution;
    private int[][] origin;
    private int mistakes;
    private Date createdTime;
    private boolean ended;
    private String reason;
    private int remainingCells;

    public Sudoku(String player, String board, String solution) {
        this.players = new ArrayList<>();
        this.players.add(player);
        this.board = new int[9][9];
        this.solution = new int[9][9];
        this.origin = new int[9][9];
        this.remainingCells = 81;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++){
                int index = i * 9 + j;
                this.board[i][j] = Character.getNumericValue(board.charAt(index));
                if (this.board[i][j] != 0) {
                    origin[i][j] = 1;
                    remainingCells -= 1;
                }
                this.solution[i][j] = Character.getNumericValue(solution.charAt(index));
            }
        }
        this.createdTime = new Date();
        this.ended = false;
    }

    public synchronized int fillCell(int x, int y, int value) {
        if (this.origin[x][y] == 1) return -1;
        int oldValue = this.board[x][y];
        this.board[x][y] = value;
        this.origin[x][y] = 2;
        if (this.solution[x][y] != value) {
            this.mistakes += 1;
            if (oldValue == this.solution[x][y]) {
                this.remainingCells += 1;
            }
            return this.mistakes;
        }
        if (oldValue == 0 || oldValue != this.solution[x][y]) {
            this.remainingCells -= 1;
        }
        return 0;
    }

    public synchronized int deleteCell(int x, int y) {
        if (this.origin[x][y] == 1) return -1;
        if (this.board[x][y] == this.solution[x][y]) {
            this.remainingCells += 1;
        }
        this.board[x][y] = 0;
        this.origin[x][y] = 0;
        return 0;
    }

    public boolean isExpired() {
        Date now = new Date();
        return now.getTime()-createdTime.getTime() > expirationTime;
    }

    public int getTimeExpired() {
        Date now = new Date();
        return (int)(now.getTime()-createdTime.getTime());
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

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;

    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getRemainingCells() {
        return remainingCells;
    }

    public void setRemainingCells(int remainingCells) {
        this.remainingCells = remainingCells;
    }
}
