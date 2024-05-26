package com.liushukov.techtask.service;

import com.liushukov.techtask.controller.MainController;
import com.liushukov.techtask.dto.Script;
import com.liushukov.techtask.dto.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ScriptServiceTest {

    @Mock
    private ScriptService scriptService;

    @InjectMocks
    private MainController mainController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testGetAllScripts() {
        Script script1 = new Script("script1", Status.QUEUED, LocalDateTime.now());
        Script script2 = new Script("script2", Status.EXECUTING, LocalDateTime.now());
        Collection<Script> scripts = Arrays.asList(script1, script2);

        when(scriptService.getAllScripts()).thenReturn(scripts);

        ResponseEntity<Collection<Script>> response = mainController.getAllScripts(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(scripts, response.getBody());
        verify(scriptService, times(1)).getAllScripts();
    }

    @Test
    public void testGetScript() {
        int scriptId = 1;
        Script script = new Script("script1", Status.QUEUED, LocalDateTime.now());

        when(scriptService.getScript(scriptId)).thenReturn(script);

        ResponseEntity<Script> response = mainController.getScript(scriptId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(script, response.getBody());
        verify(scriptService, times(1)).getScript(scriptId);
    }


    @Test
    public void testStopScript() {
        int scriptId = 1;

        when(scriptService.stopScript(scriptId)).thenReturn(true);

        ResponseEntity<String> response = mainController.stopScript(scriptId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Script was stopped by id = " + scriptId, response.getBody());
        verify(scriptService, times(1)).stopScript(scriptId);
    }

    @Test
    public void testDeleteScript() {
        int scriptId = 1;
        when(scriptService.removeScript(scriptId)).thenReturn(true);
        ResponseEntity<String> response = mainController.deleteScript(scriptId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfuly deleted script by id = " + scriptId, response.getBody());
        verify(scriptService, times(1)).removeScript(scriptId);
    }
}