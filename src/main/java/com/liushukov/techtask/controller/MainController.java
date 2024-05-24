package com.liushukov.techtask.controller;

import com.liushukov.techtask.dto.Script;
import org.springframework.web.bind.annotation.*;
import com.liushukov.techtask.service.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/scripts")
public class MainController {
    public static final String STATUS = "queued";
    @Autowired
    private ScriptService scriptService;

    @PostMapping("/execute")
    public ResponseEntity<Script> executeScript(@RequestBody String scriptBody) {
        Script script = new Script(scriptBody, STATUS, LocalDateTime.now());
        scriptService.addScript(script);
        return ResponseEntity.ok(script);
    }

    @GetMapping
    public ResponseEntity<Collection<Script>> getAllScripts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy) {

        List<Script> scripts = new ArrayList<>(scriptService.getAllScripts());

        if (status != null) {
            scripts = scripts.stream().filter(script -> script.getStatus().equals(status)).collect(Collectors.toList());
        }

        if ("id".equalsIgnoreCase(sortBy)) {
            scripts.sort((s1, s2) -> Integer.compare(s2.getId(), s1.getId()));
        } else if ("scheduledTime".equalsIgnoreCase(sortBy)) {
            scripts.sort((s1, s2) -> s2.getScheduledTime().compareTo(s1.getScheduledTime()));
        }
        return ResponseEntity.ok(scripts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Script> getScript(@PathVariable int id) {
        Script script = scriptService.getScript(id);
        if (script == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(script);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<String> stopScript(@PathVariable int id) {
        return (scriptService.stopScript(id))
                ? ResponseEntity.ok("Script was stopped by id = " + id)
                : ResponseEntity.ok("Script was finished");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteScript(@PathVariable int id) {
        return (scriptService.removeScript(id)) ? ResponseEntity.ok("Successfuly deleted script by id = " + id) :
                ResponseEntity.ok("This script is already deleted");
    }
}
