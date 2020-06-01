package com.pingxin.config;

import java.util.concurrent.ConcurrentHashMap;

import com.pingxin.model.Dudoku;
import com.pingxin.model.Sudoku;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SudokuConfig {
    @Bean
    public ConcurrentHashMap<String, Sudoku> sudokuGames() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ConcurrentHashMap<String, Dudoku> dudokuGames() {
        return new ConcurrentHashMap<>();
    }

}
