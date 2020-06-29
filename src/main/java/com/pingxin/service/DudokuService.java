package com.pingxin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingxin.agent.ComputerOpponentAssigner;
import com.pingxin.agent.MatchedDudokuDelayedRemover;
import com.pingxin.model.ApplicationUser;
import com.pingxin.model.Dudoku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DudokuService {

    @Autowired
    @Resource(name = "dudokuGames")
    private ConcurrentHashMap<String, Dudoku> dudokuGames;

    @Autowired
    private ConcurrentHashMap<String, List<ApplicationUser>> matcher;

    @Autowired
    private ConcurrentHashMap<String, Dudoku> gameLookUp;

    @Autowired
    private ApplicationUser computer;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    public List<String> getDudokuAndSolution(String level) throws IOException {
        final String uri = "https://sudoku.com/api/getLevel/" + level;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(result, Map.class);
        return (List<String>)map.get("desc");
    }

    public Dudoku getUnfishedGame(String email) {
        Dudoku dudoku = dudokuGames.get(email);
        if (dudoku != null && dudoku.isEnded()) {
            return null;
        }
        return dudoku;
    }

    public Dudoku keepWaitingForMatch(ApplicationUser me, String level) {
        List<ApplicationUser> opponents = matcher.get(level);
        for (ApplicationUser opponent : opponents) {
            if (opponent.getEmail().equals(me.getEmail())) {
                System.out.println("waiting for waited game " + opponent.getEmail());
                synchronized (opponent) {
                    try {
                        opponent.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("notified");
                Dudoku dudoku;
                if (gameLookUp.containsKey(me.getEmail())) {
                    dudoku = gameLookUp.get(me.getEmail());
                    return dudoku;
                }
                while (!dudokuGames.containsKey(me.getEmail())) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return dudokuGames.get(me.getEmail());
            }
        }
        return null;
    }

    public Dudoku findOpponentAndCreateDudokuGame(ApplicationUser me, String level) throws IOException {
        List<ApplicationUser> opponents = matcher.get(level);
        ApplicationUser opponent;
        Dudoku dudoku;
        if (!opponents.isEmpty()) {
            opponent = opponents.get(0);
            List<String> gameInfo = getDudokuAndSolution(level);
            dudoku = createDudokuGame(gameInfo.get(0), gameInfo.get(1), me, opponent);
            gameLookUp.put(opponent.getEmail(), dudoku);
            synchronized (opponent) {
                opponent.notifyAll();
            }
            return dudoku;
        }
        opponents.add(me);
        ComputerOpponentAssigner computerOpponentAssigner = new ComputerOpponentAssigner(me);
        taskExecutor.execute(computerOpponentAssigner);
        synchronized (me) {
            try {
                me.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        opponents.remove(me);
        if (gameLookUp.containsKey(me.getEmail())) {
            dudoku = gameLookUp.get(me.getEmail());
            taskExecutor.execute(new MatchedDudokuDelayedRemover(me.getEmail(), gameLookUp));
            return dudoku;
        }
        List<String> gameInfo = getDudokuAndSolution(level);
        dudoku = createDudokuGame(gameInfo.get(0), gameInfo.get(1), me, computer);
        return dudoku;
    }

    public Dudoku createDudokuGame(String board, String solution, ApplicationUser user1, ApplicationUser user2) {
        ApplicationUser[] players = new ApplicationUser[]{user1, user2};
        Dudoku dudoku = new Dudoku(players, board, solution);
        if (!user1.getEmail().equals("Computer")) dudokuGames.put(user1.getEmail(), dudoku);
        if (!user2.getEmail().equals("Computer")) dudokuGames.put(user2.getEmail(), dudoku);
        return dudoku;
    }

    public int fillCell(Dudoku dudoku, int x, int y, int value, String player) {
        return dudoku.fillCell(x, y, value, player);
    }

    public int deleteCell(Dudoku dudoku, int x, int y, String email) {
        return dudoku.deleteCell(x, y, email);
    }

    public int endGame(Dudoku dudoku, String reason, String email) {
        if (reason.equals("timeout") && dudoku.getTimeExpired() < Dudoku.getExpirationTime()-2000) return -1;
        if (reason.equals("mistake") && dudoku.getMistakes(email) < 3) return -1;
        if (reason.equals("complete") && dudoku.getRemainingCells() != 0 && dudoku.getMistakes(email) < 3) return -1;
        dudoku.setEnded(true);
        dudoku.setReason(reason);
        if (reason.equals("timeout") || reason.equals("complete")) {
            dudoku.setWinner();
        } else {
            dudoku.setLoser(email);
        }
        dudokuGames.remove(email);
        return 0;
    }

    public int getOpponentOrigin(Dudoku dudoku, String email) {
        int origin = Arrays.asList(dudoku.getPlayerEmails()).indexOf(email)+2;
        return origin == 2? 3:2;
    }

    public int getFilled(Dudoku dudoku, String email) {
        int index = Arrays.asList(dudoku.getPlayerEmails()).indexOf(email);
        return dudoku.getFilled()[index];
    }

    public int getMistakes(Dudoku dudoku, String email) {
        int index = Arrays.asList(dudoku.getPlayerEmails()).indexOf(email);
        return dudoku.getMistakes()[index];
    }

    public String getWinner(Dudoku dudoku) {
        return dudoku.getWinner();
    }

    public boolean isComputerMatch(Dudoku dudoku) {
        return Arrays.asList(dudoku.getPlayerEmails()).contains("Computer");
    }
}
