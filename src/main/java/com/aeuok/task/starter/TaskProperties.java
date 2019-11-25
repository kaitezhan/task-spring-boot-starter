package com.aeuok.task.starter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: CQ
 */
@ConfigurationProperties("aeuok.task.custom")
public class TaskProperties {

    /**
     * 是否自定义 {@link com.aeuok.task.ann.Task} 注解默认值
     */
    private boolean enable = false;

    /**
     * 任务名称
     * 默认 {className}@{filedName}
     */
    private String name = "";
    /**
     * 是否支持{@link org.springframework.transaction.annotation.Transactional}
     * 指定 taskBeanName 时无效
     */
    private boolean transactional = false;

    /**
     * 显示信息
     */
    private boolean showInfo = false;

    /**
     * 根据beanName注入 {@link com.aeuok.task.BindTaskContainerRunnable}
     */
    private String taskBeanName = "";

    /**
     * 任务等待
     */
    private boolean waitTask = true;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public void setTransactional(boolean transactional) {
        this.transactional = transactional;
    }

    public boolean isShowInfo() {
        return showInfo;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    public String getTaskBeanName() {
        return taskBeanName;
    }

    public void setTaskBeanName(String taskBeanName) {
        this.taskBeanName = taskBeanName;
    }

    public boolean isWaitTask() {
        return waitTask;
    }

    public void setWaitTask(boolean waitTask) {
        this.waitTask = waitTask;
    }

}
