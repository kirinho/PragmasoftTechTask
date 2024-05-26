package com.liushukov.techtask.controller;

import com.liushukov.techtask.dto.Script;
import com.liushukov.techtask.dto.Status;
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
    @Autowired
    private ScriptService scriptService;

    @PostMapping("/execute")
    public ResponseEntity<Script> createScript(@RequestBody String scriptBody) {
        Script script = new Script(scriptBody, Status.QUEUED, LocalDateTime.now());
        scriptService.addScript(script);
        return ResponseEntity.ok(script);
    }

    @GetMapping
    public ResponseEntity<Collection<Script>> getAllScripts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sortBy) {

        List<Script> scripts = new ArrayList<>(scriptService.getAllScripts());

        if (status != null) {
            Status filterStatus = Status.valueOf(status.toUpperCase());
            scripts = scripts.stream()
                    .filter(script -> script.getStatus() == filterStatus)
                    .collect(Collectors.toList());
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
        return (script != null) ? ResponseEntity.ok(script) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<String> stopScript(@PathVariable int id) {
        return (scriptService.stopScript(id))
                ? ResponseEntity.ok("Script was stopped by id = " + id)
                : ResponseEntity.ok("Script was already finished");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteScript(@PathVariable int id) {
        return (scriptService.removeScript(id))
                ? ResponseEntity.ok("Successfuly deleted script by id = " + id)
                : ResponseEntity.ok("Can't delete the script for some reason (already deleted or " +
                "currently running)");
    }
}
