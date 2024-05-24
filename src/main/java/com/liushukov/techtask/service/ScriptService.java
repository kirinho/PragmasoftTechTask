package com.liushukov.techtask.service;

import com.liushukov.techtask.dto.Script;
import com.liushukov.techtask.repository.ScriptRepository;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ScriptService implements ScriptRepository {
    public static final String STATUS_EXECUTING = "executing";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";
    public static final String STATUS_STOPPED = "stopped";
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<Integer, Future<?>> scriptFutures = new ConcurrentHashMap<>();
    private final Map<Integer, Script> scripts = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void addScript(Script script) {
        int generatedScriptId = counter.incrementAndGet();
        script.setId(generatedScriptId);
        scripts.put(generatedScriptId, script);
        executeScript(script);
    }
    @Override
    public Script getScript(int id) {
        return scripts.get(id);
    }

    @Override
    public Collection<Script> getAllScripts() {
        return scripts.values();
    }

    @Override
    public boolean removeScript(int id) {
        Script script = getScript(id);
        if (script != null){
            scripts.remove(id);
            scriptFutures.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public void executeScript(Script script) {
        Future<?> future = executorService.submit(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            script.setStatus(STATUS_EXECUTING);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                Engine engine = Engine.newBuilder()
                        .option("engine.WarnInterpreterOnly", "false")
                        .build();
                try (Context context = Context.newBuilder("js").engine(engine).build()) {
                    Value result = context.eval("js", script.getBody());
                    script.setExecutionTime(LocalDateTime.now());
                    script.setOutput(result.toString());
                    script.setStatus(STATUS_COMPLETED);
                }
            } catch (Exception e) {
                script.setStatus(STATUS_FAILED);
                script.setError(e.getMessage());
            } finally {
                scriptFutures.remove(script.getId());
            }
        });
        scriptFutures.put(counter.get(), future);
    }

    @Override
    public boolean stopScript(int id) {
        Future<?> future = scriptFutures.get(id);
        var script = getScript(id);
        if (future != null) {
            future.cancel(true);
            script.setStatus(STATUS_STOPPED);
            scriptFutures.remove(id);
            return true;
        }
        return false;
    }
}
