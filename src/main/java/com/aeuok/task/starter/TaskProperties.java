package com.aeuok.task.starter;

import com.aeuok.task.ann.Task;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: CQ
 */
@ConfigurationProperties("aeuok.task.global")
public class TaskProperties {
    private String taskName;
    private boolean enableTransactional;

    public TaskProperties() {
        this("",  false);
    }

    public TaskProperties(String taskName, boolean enableTransactional) {
        this.taskName = taskName;
        this.enableTransactional = enableTransactional;
    }

    public static TaskProperties convert(Task task) {
        return new TaskProperties(task.value(), task.transactional());
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isEnableTransactional() {
        return enableTransactional;
    }

    public void setEnableTransactional(boolean enableTransactional) {
        this.enableTransactional = enableTransactional;
    }

}
