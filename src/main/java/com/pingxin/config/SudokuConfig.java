package com.pingxin.config;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.pingxin.agent.CurrentDudokuRemover;
import com.pingxin.agent.CurrentSudokuRemover;
import com.pingxin.model.ApplicationUser;
import com.pingxin.model.Dudoku;
import com.pingxin.model.Sudoku;
import com.pingxin.service.DudokuService;
import com.pingxin.service.SudokuService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;

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

    @Bean
    public ConcurrentHashMap<String, List<ApplicationUser>> matcher() {
        ConcurrentHashMap<String, List<ApplicationUser>> matcher = new ConcurrentHashMap<>();
        matcher.put("easy", new LinkedList<>());
        matcher.put("medium", new LinkedList<>());
        matcher.put("hard", new LinkedList<>());
        matcher.put("expert", new LinkedList<>());
        return matcher;
    }

    @Bean
    public ConcurrentHashMap<String, Dudoku> gameLookUp() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public ApplicationUser computer() {
        return new ApplicationUser("Computer", "Computer", 0);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public CommandLineRunner scheduleSudokuRemover(@Qualifier("taskExecutor") TaskExecutor executor, ConcurrentHashMap<String, Sudoku> sudokuGames, SudokuService sudokuService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                executor.execute(new CurrentSudokuRemover(sudokuGames, sudokuService));
            }
        };
    }

    @Bean
    public CommandLineRunner scheduleDudokuRemover(@Qualifier("taskExecutor") TaskExecutor executor, ConcurrentHashMap<String, Dudoku> dudokuGames, DudokuService dudokuService) {
        return new CommandLineRunner() {
            @Override
            public void run(String... strings) throws Exception {
                executor.execute(new CurrentDudokuRemover(dudokuGames, dudokuService));
            }
        };
    }

}
