package com.aeuok.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 任务容器
 *
 * @author: CQ
 */
public class TaskContainer {
    private static final Logger log = LoggerFactory.getLogger(TaskContainer.class);
    private ObjectProvider<? extends BindTaskContainerRunnable> objectProvider;
    private List<TaskDefinition> tasks = new ArrayList<>();
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch countDownLatch;
    private ResultHolder resultHolder = new ResultHolder();
    private ExecutorService pool;
    private String taskName;
    private boolean showInfo;
    private boolean executeFlag = false;
    private String id = UUID.randomUUID().toString();

    public synchronized void execute() {
        executeFlag = true;
        if (showInfo && log.isInfoEnabled()) {
            log.info("任务【{}】开始执行，id：{}", taskName, id);
        }
        long startTime = System.currentTimeMillis();
        if (null != tasks && tasks.size() > 0) {
            int taskSize = tasks.size();
            cyclicBarrier = new CyclicBarrier(taskSize);
            countDownLatch = new CountDownLatch(taskSize);
            pool = new ThreadPoolExecutor(taskSize, taskSize, 0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(taskSize), new ThreadFactoryBuilder().setNameFormat(taskName + "-%d").build());
            for (TaskDefinition task : tasks) {
                BindTaskContainerRunnable runnable = objectProvider.getObject();
                runnable.bind(this, task);
                pool.execute(runnable);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                pool.shutdown();
                tasks.clear();
                executeFlag = false;
                if (showInfo && log.isInfoEnabled()) {
                    log.info("{}-执行完成，用时{}s", taskName, (System.currentTimeMillis() - startTime) / 1000d);
                }
            }
        }
    }

    /**
     * 清除任务
     */
    public void clear() {
        if (executeFlag) {
            log.error("任务【{}】执行中，无法清除 id：{}", taskName, id);
        } else {
            tasks.clear();
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

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public void setObjectProvider(ObjectProvider<? extends BindTaskContainerRunnable> objectProvider) {
        this.objectProvider = objectProvider;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public String getTaskName() {
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
}
