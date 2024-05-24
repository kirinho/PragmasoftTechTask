package com.liushukov.techtask.controller;

import com.liushukov.techtask.dto.Script;
import com.liushukov.techtask.service.ScriptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ScriptService scriptService;
    private Script script1;
    private Script script2;

    @BeforeEach
    void setUp() {
        script1 = new Script("print('Hello, World!')", "queued", LocalDateTime.now());
        script1.setId(1);
        script2 = new Script("print('Hello again!')", "completed", LocalDateTime.now());
        script2.setId(2);
    }

    @Test
    void testExecuteScript() throws Exception {
        Mockito.doAnswer(invocation -> {
            Script script = invocation.getArgument(0);
            script.setId(1);
            return null;
        }).when(scriptService).addScript(any(Script.class));

        mockMvc.perform(post("/api/scripts/execute")
                        .content("print('Hello, World!')")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.body", is("print('Hello, World!')")))
                .andExpect(jsonPath("$.status", is("queued")));
    }

    @Test
    void testGetAllScripts() throws Exception {
        Collection<Script> scripts = Arrays.asList(script1, script2);
        Mockito.when(scriptService.getAllScripts()).thenReturn(scripts);

        mockMvc.perform(get("/api/scripts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].status", is("queued")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].status", is("completed")));
    }

    @Test
    void testGetScript() throws Exception {
        Mockito.when(scriptService.getScript(1)).thenReturn(script1);

        mockMvc.perform(get("/api/scripts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.body", is("print('Hello, World!')")));
    }

    @Test
    void testGetScriptNotFound() throws Exception {
        Mockito.when(scriptService.getScript(1)).thenReturn(null);

        mockMvc.perform(get("/api/scripts/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testStopScript() throws Exception {
        Mockito.when(scriptService.stopScript(1)).thenReturn(true);

        mockMvc.perform(post("/api/scripts/1/stop"))
                .andExpect(status().isOk())
                .andExpect(content().string("Script was stopped by id = 1"));
    }

    @Test
    void testStopScriptAlreadyFinished() throws Exception {
        Mockito.when(scriptService.stopScript(1)).thenReturn(false);

        mockMvc.perform(post("/api/scripts/1/stop"))
                .andExpect(status().isOk())
                .andExpect(content().string("Script was finished"));
    }

    @Test
    void testDeleteScript() throws Exception {
        Mockito.when(scriptService.removeScript(1)).thenReturn(true);

        mockMvc.perform(delete("/api/scripts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfuly deleted script by id = 1"));
    }

    @Test
    void testDeleteScriptAlreadyDeleted() throws Exception {
        Mockito.when(scriptService.removeScript(1)).thenReturn(false);

        mockMvc.perform(delete("/api/scripts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("This script is already deleted"));
    }
}
