package com.aeuok.task;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author: CQ
 */
public class TaskFactory {
    private static final Logger log = LoggerFactory.getLogger(TaskFactory.class);
    private BeanFactory beanFactory;

    /**
     * 这几个属性可以通过注解注入
     *
     * @see com.aeuok.task.ann.Task
     */
    /**
     * 事务
     */
    private boolean transactional;
    /**
     * 显示信息
     */
    private boolean showInfo;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 指定bean
     */
    private String taskBeanName;
    /**
     * 等待
     */
    private boolean wait;

    public TaskFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public TaskContainer get() {
        return new TaskContainer();
    }

    public TaskContainer get(List<TaskDefinition> tasks) {
        return new TaskContainer(tasks);
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setTaskBeanName(String taskBeanName) {
        this.taskBeanName = taskBeanName;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public class TaskContainer {
        private List<TaskDefinition> tasks;
        private CyclicBarrier cyclicBarrier;
        private CountDownLatch countDownLatch;
        private ResultHolder resultHolder = new ResultHolder();
        private ExecutorService pool;


        private TaskContainer(List<TaskDefinition> tasks) {
            this.tasks = tasks;
        }

        private TaskContainer() {
            tasks = new ArrayList<>();
        }

        public void execute() {
            if (showInfo && log.isInfoEnabled()) {
                log.info("任务【{}】开始执行", getTaskName());
            }
            long startTime = System.currentTimeMillis();
            if (null != tasks && tasks.size() > 0) {
                int taskSize = tasks.size();
                cyclicBarrier = new CyclicBarrier(taskSize);
                countDownLatch = new CountDownLatch(taskSize);
                pool = new ThreadPoolExecutor(taskSize, taskSize, 0L, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat(getTaskName() + "-%d").build());
                for (TaskDefinition task : tasks) {
                    BindTaskContainerRunnable runnable;
                    if (null != taskBeanName && taskBeanName.length() > 0) {
                        runnable = (BindTaskContainerRunnable) beanFactory.getBean(taskBeanName);
                    } else if (transactional) {
                        runnable = beanFactory.getBean(TransactionalTaskRunnable.class);
                    } else {
                        runnable = beanFactory.getBean(TaskRunnable.class);
                    }
                    runnable.bind(this, task);
                    pool.execute(runnable);
                }
                if (wait) {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                pool.shutdown();
                if (showInfo && wait && log.isInfoEnabled()) {
                    log.info("【{}】-执行完成，用时{}s", getTaskName(), (System.currentTimeMillis() - startTime) / 1000d);
                }

            }
        }

        /**
         * 清除任务
         */
        public TaskContainer clear() {
            tasks.clear();
            return this;
        }

        public TaskContainer setTasks(List<TaskDefinition> tasks) {
            this.tasks = tasks;
            return this;
        }

        public TaskContainer add(TaskDefinition task) {
            tasks.add(task);
            return this;
        }

        public TaskContainer add(List<TaskDefinition> tasks) {
            this.tasks.addAll(tasks);
            return this;
        }


        public boolean isShowInfo() {
            return showInfo;
        }

        public String getTaskName() {
            return taskName;
        }

        public CyclicBarrier getCyclicBarrier() {
            return cyclicBarrier;
        }

        public CountDownLatch getCountDownLatch() {
            return countDownLatch;
        }

        public ResultHolder getResultHolder() {
            return resultHolder;
        }

        public boolean isWait() {
            return wait;
        }

    }
}
