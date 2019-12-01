package com.aeuok.task;

import com.aeuok.task.core.TaskContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: CQ
 */
public class DefaultTaskRunnable implements TaskRunnable {
    private static final Logger log = LoggerFactory.getLogger(DefaultTransactionalTaskRunnable.class);
    private TaskDefinition task;
    private TaskContainerFactory.TaskContainer taskContainer;

    @Override
    public void bind(TaskContainerFactory.TaskContainer container, TaskDefinition task) {
        this.task = task;
        this.taskContainer = container;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        if (taskContainer.isShowInfo() && log.isInfoEnabled()) {
            log.info("【{}】-开始执行", threadName);
        }
        boolean result = false;
        try {
            result = task.task();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        if (taskContainer.isShowInfo() && log.isInfoEnabled()) {
            log.info("【{}】-执行结果: {}", threadName, result ? "成功" : "失败");
        }
        if (taskContainer.isWait()) {
            taskContainer.getCountDownLatch().countDown();
        }
    }
}
