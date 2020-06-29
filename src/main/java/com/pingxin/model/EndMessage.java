package com.pingxin.model;

public class EndMessage {
    private String reason;
    private int timeExpired;
    private String winner;
    private int remainingCells;

    public EndMessage(String reason, int timeExpired, String winner, int remainingCells) {
        this.reason = reason;
        this.timeExpired = timeExpired;
        this.winner = winner;
        this.remainingCells = remainingCells;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getTimeExpired() {
        return timeExpired;
    }

    public void setTimeExpired(int timeExpired) {
        this.timeExpired = timeExpired;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getRemainingCells() {
        return remainingCells;
    }

    public void setRemainingCells(int remaining) {
        this.remainingCells = remainingCells;
    }
}
