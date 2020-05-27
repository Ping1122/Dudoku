package com.pingxin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingxin.model.Sudoku;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SudokuService {

    @Autowired
    @Resource(name = "sudokuGames")
    private ConcurrentHashMap<String, Sudoku> sudokuGames;

    public List<String> getSudokuAndSolution(String level) throws IOException {
        final String uri = "https://sudoku.com/api/getLevel/" + level;

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = mapper.readValue(result, Map.class);
        return (List<String>)map.get("desc");
    }

    public Sudoku getUnfishedGame(String email) {
        Sudoku sudoku = sudokuGames.get(email);
        if (sudoku != null && sudoku.isExpired()) {
            sudokuGames.remove(email);
            return null;
        }
        return sudoku;
    }

    public Sudoku createSudokuGame(String email, String board, String solution) {
        Sudoku sudoku = new Sudoku(email, board, solution);
        sudokuGames.put(email, sudoku);
        return sudoku;
    }

    public int fillCell(Sudoku sudoku, int x, int y, int value) {
        return sudoku.fillCell(x, y, value);
    }

    public int deleteCell(Sudoku sudoku, int x, int y) {
        return sudoku.deleteCell(x, y);
    }

    public int endGame(Sudoku sudoku, String reason, String email) {
        if (reason.equals("timeout") && sudoku.getTimeExpired() < Sudoku.getExpirationTime()-1000) return -1;
        if (reason.equals("mistake") && sudoku.getMistakes() < 3) return -1;
        if (reason.equals("complete") && sudoku.getRemainingCells() != 0 && sudoku.getMistakes() < 3) return -1;
        sudoku.setEnded(true);
        sudoku.setReason(reason);
        sudokuGames.remove(email);
        return 0;
    }
}
