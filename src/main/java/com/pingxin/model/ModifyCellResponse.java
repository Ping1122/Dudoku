package com.pingxin.model;

public class ModifyCellResponse {
    private String result;
    private int mistakes;
    private int remainingCells;

    public ModifyCellResponse(String result, int mistakes, int remainingCells) {
        this.result = result;
        this.mistakes = mistakes;
        this.remainingCells = remainingCells;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
