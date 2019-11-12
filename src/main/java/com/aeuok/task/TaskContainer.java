package com.aeuok.task;

import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

/**
 * @author: CQ
 */
public interface TaskContainer {

    void execute();

    void setShowInfo(boolean showInfo);

    void setTaskName(String taskName);

    void setTasks(List<TaskDefinition> tasks);

    void add(TaskDefinition task);

    void add(List<TaskDefinition> tasks);

    void setObjectProvider(ObjectProvider<? extends BindTaskContainerRunnable> objectProvider);
}
