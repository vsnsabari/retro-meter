package com.vsnsabari.retrometer.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.vsnsabari.retrometer.models.Member;
import com.vsnsabari.retrometer.services.EmitterService;

@RestController
@RequestMapping("feed")
@Slf4j
public class FeedController {

    private final EmitterService emitterService;

    public FeedController(EmitterService emitterService) {

        this.emitterService = emitterService;
    }

    @CrossOrigin
    @RequestMapping(value = "subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("id") String id) {
        SseEmitter emitter = null;
        try {
            var ids = id.split("_");
            var member = new Member(ids[0], ids[1]);
            emitter = emitterService.createEmitter(member);
            log.info("subscription added for member {}", member);
            emitter.send(SseEmitter.event().name("INIT"));
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
        return emitter;
    }

    @PostMapping(value = "unsubscribe/{id}")
    public ResponseEntity<Object> unsubscribe(@PathVariable("id") String id) {
        try {
            var ids = id.split("_");
            var member = new Member(ids[0], ids[1]);
            emitterService.removeEmitter(member);
            log.info("subscription removed for member {}", member);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (Exception ex) {
            log.error("Error processing to remove as action request : {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
