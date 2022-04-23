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
import org.junit.jupiter.api.BeforeEach;
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
import com.vsnsabari.retrometer.models.EventDto;
import com.vsnsabari.retrometer.services.CommentService;
import com.vsnsabari.retrometer.services.SendUpdateService;

@WebMvcTest(CommentController.class)
@Import(WebSecurityConfig.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService service;

    @MockBean
    private SendUpdateService sendUpdateService;

    @InjectMocks
    private CommentController controller;

    private static final JsonMapper JSON_MAPPER = new JsonMapper();

    @BeforeAll
    public static void setup() {
        JSON_MAPPER.findAndRegisterModules();
    }

    @BeforeEach
    public void testSetup() {
        Mockito.doNothing().when(sendUpdateService).sendCommentUpdate(anyString(), any(EventDto.class));
    }

    @Test
    @SneakyThrows
    void addComment() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.addComment(any(), anyString())).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.put("/comment/add")
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void like() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.addRemoveLikes(anyLong(), anyString(), eq(true))).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/like/%s", testComment.getId()))
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void unlike() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.addRemoveLikes(anyLong(), anyString(), eq(false))).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/unlike/%s", testComment.getId()))
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void actionItem() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.addRemoveActionItem(anyLong(), anyString(), eq(true))).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/action/add/%s", testComment.getId()))
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @SneakyThrows
    void nonActionItem() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.addRemoveActionItem(anyLong(), anyString(), eq(false))).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/action/remove/%s", testComment.getId()))
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void editComment() {
        Comment testComment = getTestComments().get(0);
        Mockito.when(service.getComment(eq(testComment.getId()))).thenReturn(testComment);
        Mockito.when(service.editComment(anyLong(), anyString(), anyString())).thenReturn(testComment);
        mockMvc.perform(MockMvcRequestBuilders.post("/comment/edit", testComment)
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(JSON_MAPPER.writeValueAsString(testComment)));
    }

    @Test
    @SneakyThrows
    void deleteComment() {
        Comment testComment = getTestComments().get(0);
        Mockito.doNothing().when(service).deleteComment(anyLong(), anyString());
        mockMvc.perform(MockMvcRequestBuilders.post(String.format("/comment/delete/%s", testComment.getId()))
                .header("X-Client-Id", "client1")
                .contentType(MediaType.APPLICATION_JSON).content(JSON_MAPPER.writeValueAsString(testComment)))
                .andDo(print())
                .andExpect(status().isOk());
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