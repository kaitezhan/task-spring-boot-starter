package com.aeuok.task.runnable;

import com.aeuok.task.core.TaskContainerFactory;
import com.aeuok.task.core.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BrokenBarrierException;

/**
 * @author: CQ
 */
public class DefaultTransactionalTaskRunnable implements TransactionalTaskRunnable {
    private static final Logger log = LoggerFactory.getLogger(DefaultTransactionalTaskRunnable.class);
    private TaskDefinition task;
    private TaskContainerFactory.TaskContainer taskContainer;

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        if (taskContainer.isShowInfo() && log.isInfoEnabled()) {
            log.info("【{}】-开始执行", threadName);
        }
        try {
            boolean result = false;
            try {
                result = task.task();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (taskContainer.isShowInfo() && log.isInfoEnabled()) {
                log.info("【{}】-执行结果: {}", threadName, result ? "成功" : "失败");
            }
            if (!result) {
                taskContainer.getResultHolder().setError(true);
            }
            try {
                taskContainer.getCyclicBarrier().await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            if (taskContainer.getResultHolder().isError()) {
                throw new RuntimeException(taskContainer.getTaskName() + " 任务失败");
            }
        } finally {
            if (taskContainer.isWait()) {
                taskContainer.getCountDownLatch().countDown();
            }
        }
    }

    @Override
    public void bind(TaskContainerFactory.TaskContainer taskContainer, TaskDefinition task) {
        this.taskContainer = taskContainer;
        this.task = task;
    }
}
