package com.liushukov.techtask.dto;

import java.util.concurrent.Future;

public class ScriptFuture {
    private final Future<?> future;
    private final Script script;

    public ScriptFuture( Script script, Future<?> future) {
        this.script = script;
        this.future = future;
    }
    public Future<?> getFuture() {
        return future;
    }

    public Script getScript() {
        return script;
    }
}
