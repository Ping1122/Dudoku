package com.pingxin.model;

public class FillMessage {
    private int x;
    private int y;
    private int value;
    private int filled;
    private int mistakes;
    private int remainingCells;

    public FillMessage(int x, int y, int value, int filled, int mistakes, int remainingCells) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.filled = filled;
        this.mistakes = mistakes;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getFilled() {
        return filled;
    }

    public void setFilled(int filled) {
        this.filled = filled;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public int getRemainingCells() {
        return remainingCells;
    }

    public void setRemainingCells(int remainingCells) {
        this.remainingCells = remainingCells;
    }
}
