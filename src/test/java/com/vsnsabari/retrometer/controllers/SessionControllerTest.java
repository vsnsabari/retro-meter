package com.vsnsabari.retrometer.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.vsnsabari.retrometer.DummyFactory;
import com.vsnsabari.retrometer.config.WebSecurityConfig;
import com.vsnsabari.retrometer.entities.Session;
import com.vsnsabari.retrometer.services.SessionService;

@WebMvcTest(SessionController.class)
@Import(WebSecurityConfig.class)
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SessionService service;

    @InjectMocks
    private SessionController controller;

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @Test
    @SneakyThrows
    void createSession() {
        Session session = DummyFactory.getTestSession("sessionController");
        session.setCreatedDate(null);
        Mockito.when(service.createSession(any())).thenReturn(session);
        mockMvc.perform(MockMvcRequestBuilders.put("/session/create")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(session)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(session)));
    }

    @Test
    @SneakyThrows
    void getByDate() {
        var sessions = getTestSessions();
        Mockito.when(service.getAllSessionByCreatedDate(LocalDate.now())).thenReturn(sessions);
        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/session/getbydate/%s", date)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(sessions)));
    }

    private Session[] getTestSessions() {
        var session = DummyFactory.getTestSession("sessionController");
        return new Session[]{session};
    }
}