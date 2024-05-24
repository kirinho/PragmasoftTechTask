package com.liushukov.techtask.repository;

import com.liushukov.techtask.dto.Script;
import java.util.Collection;


public interface ScriptRepository {
    public void addScript(Script script);

    public Script getScript(int id);

    public Collection<Script> getAllScripts();

    public boolean removeScript(int id);
    public void executeScript(Script script);
    public boolean stopScript(int id);
}
