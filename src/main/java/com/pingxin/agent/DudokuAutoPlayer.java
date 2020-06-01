package com.pingxin.agent;

import com.pingxin.model.Dudoku;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class DudokuAutoPlayer extends Thread {

    private Dudoku dudoku;
    private int intervalAverage = 30;
    private int intervalStd = 2;

    public DudokuAutoPlayer(Dudoku dudoku) {
        super();
        this.dudoku = dudoku;
    }

    public void run() {
        while (dudoku.getRemainingCells() != 0 && !dudoku.isEnded()) {
            double interval = ThreadLocalRandom.current().nextGaussian() * intervalStd + intervalAverage;
            try {
                currentThread().sleep((int)interval * 1000);
            } catch (InterruptedException exception) {
            }
            int[] hint = dudoku.randomHint();
            if (hint == null) break;
            dudoku.fillCell(hint[0], hint[1], hint[2], "Computer");
        }
        if (dudoku.getRemainingCells() == 0) {
            dudoku.setEnded(true);
            dudoku.setReason("completed");
        }
    }
}
