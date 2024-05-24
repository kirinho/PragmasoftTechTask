package com.liushukov.techtask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Script {
    private int id;
    private String status;
    private String body;
    private LocalDateTime scheduledTime;
    private LocalDateTime executionTime;
    private String output;
    private String error;

    public Script(String body, String status, LocalDateTime scheduledTime){
        this.body = body;
        this.status = status;
        this.scheduledTime = scheduledTime;
    }
}
