package com.aeuok.task;

/**
 * @author: CQ
 */
public interface BindTaskContainerRunnable extends Runnable {
    /**
     * 绑定任务
     *
     * @param container 任务容器
     * @param task      任务
     */
    void bind(TaskContainer container, TaskDefinition task);

}
