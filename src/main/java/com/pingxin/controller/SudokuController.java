package com.pingxin.controller;

import com.pingxin.model.*;
import com.pingxin.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.pingxin.service.SudokuService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class SudokuController {

    @Autowired
    private SudokuService sudokuService;

    @Autowired
    private ApplicationUserService applicationUserService;

    @GetMapping("/api/sudoku/{level}")
    public ResponseEntity<Object> getSudoku(@PathVariable(value="level") String level, HttpServletRequest request)
            throws Exception{
        final List<String> supportedLevels = Arrays.asList("easy", "medium", "hard", "expert");
        if (!supportedLevels.contains(level)) {
            return new ResponseEntity<>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Invalid Difficulty Level",
                    "/api/sudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        String email = applicationUserService.getEmailfromRequest(request);
        Sudoku sudoku = sudokuService.getUnfishedGame(email);
        if (sudoku != null) {
            return new ResponseEntity<>(sudoku, HttpStatus.OK);
        }
        List<String> sudokuAndSolution = sudokuService.getSudokuAndSolution(level);
        sudoku = sudokuService.createSudokuGame(email, sudokuAndSolution.get(0), sudokuAndSolution.get(1));
        return new ResponseEntity<>(sudoku, HttpStatus.OK);
    }

    @PutMapping("api/sudoku/cell")
    public ResponseEntity<Object> fillCell(@Valid @RequestBody ModifyCellRequest modifyCellRequest, HttpServletRequest request)
            throws Exception {
        String email = applicationUserService.getEmailfromRequest(request);
        Sudoku sudoku = sudokuService.getUnfishedGame(email);
        if (sudoku == null) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "The game has already ended",
                    "api/sudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = sudokuService.fillCell(sudoku, modifyCellRequest.getX(), modifyCellRequest.getY(), modifyCellRequest.getValue());
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to fill a pre-filled cell",
                    "api/sudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        if (status > 0) {
            return new ResponseEntity<Object>(new ModifyCellResponse("incorrect", status, sudoku.getRemainingCells()), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(new ModifyCellResponse("correct", 0, sudoku.getRemainingCells()), HttpStatus.OK);
    }

    @DeleteMapping("api/sudoku/cell")
    public ResponseEntity<Object> eraseCell(@Valid @RequestBody ModifyCellRequest modifyCellRequest, HttpServletRequest request){
        String email = applicationUserService.getEmailfromRequest(request);
        Sudoku sudoku = sudokuService.getUnfishedGame(email);
        if (sudoku == null) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "The game has already ended",
                    "api/sudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = sudokuService.deleteCell(sudoku, modifyCellRequest.getX(), modifyCellRequest.getY());
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to delete a pre-filled cell",
                    "api/sudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Object>(new ModifyCellResponse("ok", 0, sudoku.getRemainingCells()), HttpStatus.OK);
    }

    @PutMapping("/api/sudoku")
    public ResponseEntity<Object> endGame(@RequestBody EndGameRequest endGameRequest, HttpServletRequest request) {
        String email = applicationUserService.getEmailfromRequest(request);
        Sudoku sudoku = sudokuService.getUnfishedGame(email);
        if (sudoku == null) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "The game has already ended",
                    "api/sudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        List<String> validReasons = Arrays.asList("timeout", "mistake", "complete", "giveup");
        String reason = endGameRequest.getReason();
        if (!validReasons.contains(reason)) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "End game reason not supported",
                    "api/sudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = sudokuService.endGame(sudoku, reason, email);
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "End game reason not valid",
                    "api/sudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok("");
    }
}
