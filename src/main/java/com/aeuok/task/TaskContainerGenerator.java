package com.aeuok.task;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author: CQ
 */
public class TaskContainerGenerator {
    private ObjectFactory<TaskContainer> factory;

    public TaskContainer get(String taskName) {
        TaskContainer taskContainer = factory.getObject();
        taskContainer.setTaskName(taskName);
        return taskContainer;
    }

    public TaskContainer get(String taskName, List<TaskDefinition> tasks) {
        TaskContainer taskContainer = factory.getObject();
        taskContainer.setTasks(tasks);
        taskContainer.setTaskName(taskName);
        return taskContainer;
    }

    public void setFactory(ObjectFactory<TaskContainer> factory) {
        this.factory = factory;
    }
}
