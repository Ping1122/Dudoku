package com.pingxin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingxin.model.Dudoku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DudokuService {

    @Autowired
    @Resource(name = "dudokuGames")
    private ConcurrentHashMap<String, Dudoku> dudokuGames;

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
        if (dudoku != null && dudoku.isExpired()) {
            return null;
        }
        return dudoku;
    }

    public Dudoku createDudokuGame(String board, String solution, String player1, String player2) {
        String[] players = new String[]{player1, player2};
        Dudoku dudoku = new Dudoku(players, board, solution);
        if (!player1.equals("Computer")) dudokuGames.put(player1, dudoku);
        if (!player2.equals("Computer")) dudokuGames.put(player2, dudoku);
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
        dudokuGames.remove(email);
        return 0;
    }
}
