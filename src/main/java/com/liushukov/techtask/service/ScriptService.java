package com.liushukov.techtask.service;

import com.liushukov.techtask.dto.Script;
import com.liushukov.techtask.dto.ScriptFuture;
import com.liushukov.techtask.dto.Status;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ScriptService {
    private final ExecutorService executorService;
    private final ScheduledExecutorService executor;
    private final ConcurrentHashMap<Integer, ScriptFuture> scriptFutureMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Script> outputs = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    public ScriptService(){
        this.executorService = Executors.newCachedThreadPool();
        this.executor = Executors.newScheduledThreadPool(1);
        printScriptsOutput();
    }


    public void addScript(Script script) {
        int generatedScriptId = counter.incrementAndGet();
        script.setId(generatedScriptId);
        executeScript(script);
    }

    public Script getScript(int id) {
        ScriptFuture scriptFuture = scriptFutureMap.get(id);
        return scriptFuture != null ? scriptFuture.getScript() : null;
    }


    public Collection<Script> getAllScripts() {
        return scriptFutureMap.values().stream().map(ScriptFuture::getScript).toList();
    }

    public boolean removeScript(int id) {
        var scriptFuture = scriptFutureMap.get(id);
        if (scriptFuture != null) {
            var script = scriptFuture.getScript();
            if (script.getStatus() == Status.COMPLETED || script.getStatus() == Status.FAILED
                    || script.getStatus() == Status.STOPPED) {
                scriptFutureMap.remove(id);
                return true;
            }
        }
        return false;
    }

    public void executeScript(Script script) {
        Future<?> future = executorService.submit(() -> {
            script.setStatus(Status.EXECUTING);
            try {
                Engine engine = Engine.newBuilder()
                        .option("engine.WarnInterpreterOnly", "false")
                        .build();
                try (Context context = Context.newBuilder("js").engine(engine).build()) {
                    Value result = context.eval("js", script.getBody());
                    script.setExecutionTime(LocalDateTime.now());
                    script.setOutput(result.toString());
                    script.setStatus(Status.COMPLETED);
                }
            } catch (Exception e) {
                script.setStatus(Status.FAILED);
                script.setError(e.getMessage());
            } finally {
                outputs.put(script.getId(), script);
            }
        });
        scriptFutureMap.put(script.getId(), new ScriptFuture(script, future));
    }

    public boolean stopScript(int id) {
        var scriptFuture = scriptFutureMap.get(id);
        if (scriptFuture != null) {
            Future<?> future = scriptFuture.getFuture();
            if (future != null && (scriptFuture.getScript().getStatus() == Status.EXECUTING
                    || scriptFuture.getScript().getStatus() == Status.QUEUED)) {
                future.cancel(true);
                scriptFuture.getScript().setStatus(Status.STOPPED);
                return true;
            }
        }
        return false;
    }

    public void printScriptsOutput() {
        executor.scheduleAtFixedRate(() -> {
            if (!outputs.isEmpty()){
                for (Map.Entry<Integer, Script> entry : outputs.entrySet()) {
                    int scriptId = entry.getKey();
                    Script script = entry.getValue();
                    System.out.println("id: " + scriptId + "\nstatus: " + script.getStatus()
                            + "\noutput: " + script.getOutput() + "\nerror: " + script.getError()
                            + "\nexecution time: " + script.getExecutionTime() + "\n---"
                    );
                }
                outputs.clear();
            }
        }, 0, 60, TimeUnit.SECONDS);
    }
}
