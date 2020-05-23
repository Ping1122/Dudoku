package com.pingxin.controller;

import com.pingxin.model.FillCellRequest;
import com.pingxin.model.FillCellResponse;
import com.pingxin.model.Sudoku;
import com.pingxin.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import com.pingxin.model.ErrorResponse;
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

    @PutMapping("api/sudoku")
    public ResponseEntity<Object> fillCell(@Valid @RequestBody FillCellRequest fillCellRequest, HttpServletRequest request)
            throws Exception {
        String email = applicationUserService.getEmailfromRequest(request);
        Sudoku sudoku = sudokuService.getUnfishedGame(email);
        if (sudoku == null) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "The game has already ended",
                    "api/sudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = sudokuService.fillCell(sudoku, fillCellRequest.getX(), fillCellRequest.getY(), fillCellRequest.getValue());
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to fill a filled cell",
                    "api/sudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        if (status > 0) {
            return new ResponseEntity<Object>(new FillCellResponse("incorrect", status), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(new FillCellResponse("correct", 0), HttpStatus.OK);
    }
}
