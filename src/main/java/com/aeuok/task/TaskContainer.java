package com.aeuok.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 任务容器
 *
 * @author: CQ
 */
public class TaskContainer {
    private static final Logger log = LoggerFactory.getLogger(TaskContainer.class);
    private ObjectFactory<TaskRunnable> runnableFactory;
    private List<TaskDefinition> tasks = new ArrayList<>();
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch countDownLatch;
    private ResultHolder resultHolder = new ResultHolder();
    private ExecutorService pool;
    private String taskName;
    private boolean debugger;

    public void execute() {
        long startTime = System.currentTimeMillis();
        if (null != tasks && tasks.size() > 0) {
            int size = tasks.size();
            cyclicBarrier = new CyclicBarrier(size);
            countDownLatch = new CountDownLatch(size);
            pool = new ThreadPoolExecutor(size, size, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(size), new ThreadFactoryBuilder().setNameFormat(taskName + "-%d").build());
            for (TaskDefinition task : tasks) {
                TaskRunnable runnable = runnableFactory.getObject();
                runnable.bind(this, task);
                pool.execute(runnable);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                pool.shutdown();
            }
        }
        if (debugger) {
            log.info("{}-执行完成，用时{}s", taskName, (System.currentTimeMillis() - startTime) / 1000d);
        }
    }

    public void setTasks(List<TaskDefinition> tasks) {
        this.tasks = tasks;
    }

    public void add(TaskDefinition task) {
        tasks.add(task);
    }

    public void add(List<TaskDefinition> tasks) {
        this.tasks.addAll(tasks);
    }

    protected void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    protected String getTaskName() {
        return taskName;
    }

    protected CyclicBarrier getCyclicBarrier() {
        return cyclicBarrier;
    }

    protected CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    protected ResultHolder getResultHolder() {
        return resultHolder;
    }

    public void setDebugger(boolean debugger) {
        this.debugger = debugger;
    }

    public void setRunnableFactory(ObjectFactory<TaskRunnable> runnableFactory) {
        this.runnableFactory = runnableFactory;
    }
}
