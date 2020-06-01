package com.pingxin.model;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class Dudoku {

    private static final int expirationTime = 20*60*1000;
    private String[] players;
    private int[][] board;
    private int[][] solution;
    private int[][] origin;
    private int[] mistakes;
    private int[] filled;
    private Date createdTime;
    private boolean ended;
    private String reason;
    private int remainingCells;

    public Dudoku(String[] players, String board, String solution) {
        this.players = players;
        this.board = new int[9][9];
        this.solution = new int[9][9];
        this.origin = new int[9][9];
        this.mistakes = new int[2];
        this.filled = new int[2];
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

    public synchronized int fillCell(int x, int y, int value, String player) {
        int origin = Arrays.asList(this.players).indexOf(player) + 2;
        if (this.origin[x][y] == 1) return -1;
        if (this.board[x][y] == this.solution[x][y]) return -2;
        int oldValue = this.board[x][y];
        this.board[x][y] = value;
        this.origin[x][y] = origin;
        if (this.solution[x][y] != value) {
            this.mistakes[origin-2] += 1;
            if (oldValue == this.solution[x][y]) {
                this.remainingCells += 1;
            }
            return this.mistakes[origin-2];
        }
        this.filled[origin-2] += 1;
        if (oldValue == 0 || oldValue != this.solution[x][y]) {
            this.remainingCells -= 1;
        }
        return 0;
    }

    public synchronized int[] randomHint() {
        System.out.println(this.remainingCells);
        int target = ThreadLocalRandom.current().nextInt(this.remainingCells);
        System.out.println(target);
        int count = 0;
        int x = 0;
        int y = 0;
        loop:
        for (x = 0; x < 9; x++) {
            for (y = 0; y < 9; y++) {
                if (board[x][y] != solution[x][y]) {
                    if (count == target) break loop;
                    count += 1;
                }
            }
        }
        System.out.println(x);
        System.out.println(y);
        return new int[]{x, y, solution[x][y]};
    }

    public synchronized int deleteCell(int x, int y, String player) {
        int origin = Arrays.asList(this.players).indexOf(player) + 2;
        if (this.origin[x][y] == 1) return -1;
        if (this.origin[x][y] != origin && this.board[x][y] == this.solution[x][y]) return -2;
        if (this.board[x][y] == this.solution[x][y]) {
            this.remainingCells += 1;
            this.filled[origin-2] -= 1;
        }
        this.board[x][y] = 0;
        this.origin[x][y] = 0;
        return 0;
    }

    public boolean isExpired() {
        Date now = new Date();
        return now.getTime()-createdTime.getTime() > expirationTime + 2000;
    }

    public int getTimeExpired() {
        Date now = new Date();
        return (int)(now.getTime()-createdTime.getTime());
    }

    public static int getExpirationTime() {
        return expirationTime;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
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

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public synchronized String getReason() {
        return reason;
    }

    public synchronized void setReason(String reason) {
        this.reason = reason;
    }

    public synchronized int getRemainingCells() {
        return remainingCells;
    }

    public synchronized void setRemainingCells(int remainingCells) {
        this.remainingCells = remainingCells;
    }

    public int[] getMistakes() {
        return this.mistakes;
    }

    public int getMistakes(String player) {
        int origin = Arrays.asList(this.players).indexOf(player);
        return mistakes[origin];
    }

    public void setMistakes(int[] mistakes) {
        this.mistakes = mistakes;
    }

    public int[] getFilled() {
        return filled;
    }

    public void setFilled(int[] filled) {
        this.filled = filled;
    }
}
