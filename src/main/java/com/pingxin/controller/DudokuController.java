package com.pingxin.controller;

import com.pingxin.agent.DudokuAutoPlayer;
import com.pingxin.model.*;
import com.pingxin.service.ApplicationUserService;
import com.pingxin.service.DudokuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate brokerMessagingTemplate;

    @Autowired
    @Qualifier("taskExecutor")
    private TaskExecutor taskExecutor;

    @GetMapping("/api/dudoku/{level}")
    public ResponseEntity<Object> getDudoku(@PathVariable(value="level") String level, HttpServletRequest request)
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
        String email = applicationUserService.getEmailFromRequest(request);
        String username = applicationUserService.getUsernameFromRequest(request);
        ApplicationUser user = new ApplicationUser(email, username, 0);
        Dudoku dudoku = dudokuService.getUnfishedGame(email);
        if (dudoku != null) {
            return new ResponseEntity<>(dudoku, HttpStatus.OK);
        }
        dudoku = dudokuService.keepWaitingForMatch(user, level);
        if (dudoku != null) {
            return new ResponseEntity<>(dudoku, HttpStatus.OK);
        }
        dudoku = dudokuService.findOpponentAndCreateDudokuGame(user, level);
        if (dudokuService.isComputerMatch(dudoku)) {
            DudokuAutoPlayer autoPlayer = new DudokuAutoPlayer(dudoku, brokerMessagingTemplate, dudokuService);
            taskExecutor.execute(autoPlayer);
        }
        return new ResponseEntity<>(dudoku, HttpStatus.OK);
    }

    @PutMapping("api/dudoku/cell")
    public ResponseEntity<Object> fillCell(@Valid @RequestBody ModifyCellRequest modifyCellRequest, HttpServletRequest request)
            throws Exception {
        String email = applicationUserService.getEmailFromRequest(request);
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
            return new ResponseEntity<>(new ErrorResponse(
                    new Date(),
                    400,
                    "Bad Request",
                    "Attempt to fill a filled cell",
                    "api/dudoku/cell"
            ), HttpStatus.BAD_REQUEST);
        }
        int opponentOrigin = dudokuService.getOpponentOrigin(dudoku, email);
        String messageEndPoint = "/topic/fill/"+dudoku.getId()+"/"+opponentOrigin;
        FillMessage fillMessage = new FillMessage(
                modifyCellRequest.getX(),
                modifyCellRequest.getY(),
                modifyCellRequest.getValue(),
                dudokuService.getFilled(dudoku, email),
                dudokuService.getMistakes(dudoku, email),
                dudoku.getRemainingCells()
        );
        brokerMessagingTemplate.convertAndSend(messageEndPoint, fillMessage);
        if (status > 0) {
            return new ResponseEntity<>(new ModifyCellResponse("incorrect", status, dudoku.getRemainingCells()), HttpStatus.OK);
        }
        return new ResponseEntity<>(new ModifyCellResponse("correct", 0, dudoku.getRemainingCells()), HttpStatus.OK);
    }

    @DeleteMapping("api/dudoku/cell")
    public ResponseEntity<Object> eraseCell(@Valid @RequestBody ModifyCellRequest modifyCellRequest, HttpServletRequest request){
        String email = applicationUserService.getEmailFromRequest(request);
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
        int opponentOrigin = dudokuService.getOpponentOrigin(dudoku, email);
        String messageEndPoint = "/topic/delete/"+dudoku.getId()+"/"+opponentOrigin;
        DeleteMessage deleteMessage = new DeleteMessage(
                modifyCellRequest.getX(),
                modifyCellRequest.getY(),
                dudokuService.getFilled(dudoku, email),
                dudoku.getRemainingCells()
        );
        brokerMessagingTemplate.convertAndSend(messageEndPoint, deleteMessage);
        return new ResponseEntity<Object>(new ModifyCellResponse("ok", 0, dudoku.getRemainingCells()), HttpStatus.OK);
    }

    @PutMapping("/api/dudoku")
    public ResponseEntity<Object> endGame(@RequestBody EndGameRequest endGameRequest, HttpServletRequest request) {
        String email = applicationUserService.getEmailFromRequest(request);
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
        Map<String, Object> response = new HashMap<>();
        String winner = dudokuService.getWinner(dudoku);
        response.put("timeExpired", dudoku.getTimeExpired());
        response.put("winner", winner);
        response.put("remaining", dudoku.getRemainingCells());
        int opponentOrigin = dudokuService.getOpponentOrigin(dudoku, email);
        String messageEndPoint = "/topic/end/"+dudoku.getId()+"/"+opponentOrigin;
        EndMessage endMessage = new EndMessage(reason, dudoku.getTimeExpired(), winner, dudoku.getRemainingCells());
        brokerMessagingTemplate.convertAndSend(messageEndPoint, endMessage);
        return new ResponseEntity<Object>(response, HttpStatus.OK);
    }
}
