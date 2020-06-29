package com.pingxin.model;

public class DeleteMessage {
    private int x;
    private int y;
    private int filled;
    private int remainingCells;

    public DeleteMessage(int x, int y, int filled, int remainingCells) {
        this.x = x;
        this.y = y;
        this.filled = filled;
        this.remainingCells = remainingCells;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getFilled() {
        return filled;
    }

    public void setFilled(int filled) {
        this.filled = filled;
    }

    public int getRemainingCells() {
        return remainingCells;
    }

    public void setRemainingCells(int remainingCells) {
        this.remainingCells = remainingCells;
    }
}
