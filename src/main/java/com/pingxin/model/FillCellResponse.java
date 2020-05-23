package com.pingxin.model;

public class FillCellResponse {
    private String result;
    private int mistakes;

    public FillCellResponse(String result, int mistakes) {
        this.result = result;
        this.mistakes = mistakes;
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
}
