package com.aeuok.task.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: CQ
 */
@ConfigurationProperties("aeuok.task")
public class TaskProperties {
    private boolean debugger = false;
    private boolean enableDefaultTransactional = false;

    public boolean isEnableDefaultTransactional() {
        return enableDefaultTransactional;
    }

    public void setEnableDefaultTransactional(boolean enableDefaultTransactional) {
        this.enableDefaultTransactional = enableDefaultTransactional;
    }

    public boolean isDebugger() {
        return debugger;
    }

    public void setDebugger(boolean debugger) {
        this.debugger = debugger;
    }
}
