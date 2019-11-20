package com.aeuok.task.starter;

import com.aeuok.task.ann.Task;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: CQ
 */
@ConfigurationProperties("aeuok.task.global")
public class TaskProperties {
    private String taskNamePrefix;

    public String getTaskNamePrefix() {
        return taskNamePrefix;
    }

    public void setTaskNamePrefix(String taskNamePrefix) {
        this.taskNamePrefix = taskNamePrefix;
    }
}
