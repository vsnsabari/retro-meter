package com.vsnsabari.retrometer.controllers;

import java.time.LocalDate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vsnsabari.retrometer.entities.Session;
import com.vsnsabari.retrometer.exceptions.SessionCreationException;
import com.vsnsabari.retrometer.exceptions.SessionNotFoundException;
import com.vsnsabari.retrometer.services.SessionService;

@RestController
@RequestMapping("session")
@Slf4j
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {

        this.sessionService = sessionService;
    }

    @PutMapping(value = "create")
    public ResponseEntity<Object> createSession(@RequestBody Session session) {
        try {
            log.info("Received Request to create session {}", session);
            return new ResponseEntity<>(sessionService.createSession(session), HttpStatus.CREATED);
        } catch (SessionCreationException ex) {
            log.error("Error processing request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @RequestMapping("getbydate/{date}")
    public ResponseEntity<Object> getByDate(@PathVariable("date")
                                                          @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        log.info("Received Request to getByDate {}", date);
        return new ResponseEntity<>(sessionService.getAllSessionByCreatedDate(date), HttpStatus.OK);
    }

    @RequestMapping("getbyid/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id")String sessionId) {
        try {
            log.info("Received Request to getById {}", sessionId);
            return new ResponseEntity<>(sessionService.getBySessionId(sessionId), HttpStatus.OK);
        } catch (SessionNotFoundException ex) {
            log.error("Error processing request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
