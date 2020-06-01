package com.pingxin.controller;

import com.pingxin.agent.DudokuAutoPlayer;
import com.pingxin.model.*;
import com.pingxin.service.ApplicationUserService;
import com.pingxin.service.DudokuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
public class DudokuController {

    @Autowired
    private DudokuService dudokuService;

    @Autowired
    private ApplicationUserService applicationUserService;

    @GetMapping("/api/dudoku/{level}")
    public ResponseEntity<Object> getSudoku(@PathVariable(value="level") String level, HttpServletRequest request)
            throws Exception{
        final List<String> supportedLevels = Arrays.asList("easy", "medium", "hard", "expert");
        if (!supportedLevels.contains(level)) {
            return new ResponseEntity<>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Invalid Difficulty Level",
                    "/api/dudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        String email = applicationUserService.getEmailfromRequest(request);
        Dudoku dudoku = dudokuService.getUnfishedGame(email);
        if (dudoku != null) {
            return new ResponseEntity<>(dudoku, HttpStatus.OK);
        }
        List<String> dudokuAndSolution = dudokuService.getDudokuAndSolution(level);
        dudoku = dudokuService.createDudokuGame(dudokuAndSolution.get(0), dudokuAndSolution.get(1), email, "Computer");
        DudokuAutoPlayer autoPlayer = new DudokuAutoPlayer(dudoku);
        autoPlayer.start();
        return new ResponseEntity<>(dudoku, HttpStatus.OK);
    }

    @PutMapping("api/dudoku/cell")
    public ResponseEntity<Object> fillCell(@Valid @RequestBody ModifyCellRequest modifyCellRequest, HttpServletRequest request)
            throws Exception {
        String email = applicationUserService.getEmailfromRequest(request);
        Dudoku dudoku = dudokuService.getUnfishedGame(email);
        if (dudoku == null) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "The game has already ended",
                    "api/dudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = dudokuService.fillCell(dudoku, modifyCellRequest.getX(), modifyCellRequest.getY(), modifyCellRequest.getValue(), email);
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to fill a pre-filled cell",
                    "api/dudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        if (status == -2) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to fill a filled cell",
                    "api/dudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        if (status > 0) {
            return new ResponseEntity<Object>(new ModifyCellResponse("incorrect", status, dudoku.getRemainingCells()), HttpStatus.OK);
        }
        return new ResponseEntity<Object>(new ModifyCellResponse("correct", 0, dudoku.getRemainingCells()), HttpStatus.OK);
    }

    @DeleteMapping("api/dudoku/cell")
    public ResponseEntity<Object> eraseCell(@Valid @RequestBody ModifyCellRequest modifyCellRequest, HttpServletRequest request){
        String email = applicationUserService.getEmailfromRequest(request);
        Dudoku dudoku = dudokuService.getUnfishedGame(email);
        if (dudoku == null) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "The game has already ended",
                    "api/sudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = dudokuService.deleteCell(dudoku, modifyCellRequest.getX(), modifyCellRequest.getY(), email);
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to delete a pre-filled cell",
                    "api/dudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        if (status == -2) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to delete a correct cell filled by opponent",
                    "api/dudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Object>(new ModifyCellResponse("ok", 0, dudoku.getRemainingCells()), HttpStatus.OK);
    }

    @PutMapping("/api/dudoku")
    public ResponseEntity<Object> endGame(@RequestBody EndGameRequest endGameRequest, HttpServletRequest request) {
        String email = applicationUserService.getEmailfromRequest(request);
        Dudoku dudoku = dudokuService.getUnfishedGame(email);
        if (dudoku == null) {
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
                    "api/dudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        int status = dudokuService.endGame(dudoku, reason, email);
        if (status == -1) {
            return new ResponseEntity<Object>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "End game reason not valid",
                    "api/sudoku"
            ), HttpStatus.BAD_REQUEST);
        }
        Map<String, Integer> response = new HashMap<>();
        response.put("timeExpired", dudoku.getTimeExpired());
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }
}
