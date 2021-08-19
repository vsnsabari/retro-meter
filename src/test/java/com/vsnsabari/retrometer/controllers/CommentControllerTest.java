package com.vsnsabari.retrometer.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import lombok.SneakyThrows;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeAll;
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
import com.vsnsabari.retrometer.entities.Comment;
import com.vsnsabari.retrometer.services.CommentService;

@WebMvcTest(CommentController.class)
@Import(WebSecurityConfig.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService service;

    @InjectMocks
    private CommentController controller;

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @BeforeAll
    public static void setup() {
        JSON_MAPPER.findAndRegisterModules();
    }

    @Test
    @SneakyThrows
    void addComment() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.addComment(any())).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.put("/comment/add")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void addUpVote() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.addUpVote(anyLong())).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/upvote/%s", testComment.getId()))
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void addDownVote() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.addDownVote(anyLong())).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/downvote/%s", testComment.getId()))
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void getBySessionId() {
        var testComment = getTestComments().get(0);
        Comment[] testComments = {testComment};
        Mockito.when(service.getAllCommentBySessionId(anyString())).thenReturn(testComments);
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/comment/getbysession/%s", testComment.getSessionId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComments)));

    }

    private List<Comment> getTestComments() {
        var session = DummyFactory.getTestSession("commentController");
        session.setSessionId(UUID.randomUUID().toString());
        var comment = DummyFactory.getTestComment("commentController", session.getSessionId());
        comment.setId(1L);
        return Lists.list(comment);
    }
}