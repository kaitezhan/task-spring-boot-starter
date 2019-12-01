package com.aeuok.task.runnable;

import com.aeuok.task.core.TaskContainerFactory.TaskContainer;
import com.aeuok.task.core.TaskDefinition;

/**
 * @author: CQ
 */
public interface BindTaskContainerRunnable extends Runnable {
    /**
     * 绑定任务
     *
     * @param taskContainer 任务容器
     * @param task          任务
     */
    void bind(TaskContainer taskContainer, TaskDefinition task);

}
