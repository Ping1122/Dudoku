package com.pingxin.agent;

import com.pingxin.model.Dudoku;
import com.pingxin.model.EndMessage;
import com.pingxin.model.FillMessage;
import com.pingxin.service.DudokuService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.ThreadLocalRandom;

public class DudokuAutoPlayer implements Runnable {

    private Dudoku dudoku;
    private int intervalAverage = 5;
    private int intervalStd = 1;
    private SimpMessagingTemplate brokerMessagingTemplate;
    private DudokuService dudokuService;

    public DudokuAutoPlayer(Dudoku dudoku, SimpMessagingTemplate brokerMessagingTemplate, DudokuService dudokuService) {
        this.brokerMessagingTemplate = brokerMessagingTemplate;
        this.dudokuService = dudokuService;
        this.dudoku = dudoku;
    }

    public void run() {
        while (dudoku.getRemainingCells() != 0 && !dudoku.isEnded()) {
            double interval = ThreadLocalRandom.current().nextGaussian() * intervalStd + intervalAverage;
            try {
                Thread.sleep((int)interval * 1000);
            } catch (InterruptedException exception) {
            }
            int[] hint = dudoku.randomHint();
            if (hint == null) break;
            dudoku.fillCell(hint[0], hint[1], hint[2], "Computer");
            int opponentOrigin = dudokuService.getOpponentOrigin(dudoku, "Computer");
            String messageEndPoint = "/topic/fill/"+dudoku.getId()+"/"+opponentOrigin;
            FillMessage fillMessage = new FillMessage(
                    hint[0], hint[1], hint[2],
                    dudokuService.getFilled(dudoku, "Computer"),
                    dudokuService.getMistakes(dudoku, "Computer"),
                    dudoku.getRemainingCells()
            );
            brokerMessagingTemplate.convertAndSend(messageEndPoint, fillMessage);
        }
        if (!dudoku.isEnded()) {
            dudoku.setEnded(true);
            dudoku.setReason("completed");
            int opponentOrigin = dudokuService.getOpponentOrigin(dudoku, "Computer");
            String messageEndPoint = "/topic/end/"+dudoku.getId()+"/"+opponentOrigin;
            String winner = dudokuService.getWinner(dudoku);
            EndMessage endMessage = new EndMessage("complete", dudoku.getTimeExpired(), winner, dudoku.getRemainingCells());
            brokerMessagingTemplate.convertAndSend(messageEndPoint, endMessage);
        }
    }
}
