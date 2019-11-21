package com.aeuok.task.ann;

import java.lang.annotation.*;

/**
 * @author: CQ
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface Task {
    /**
     * 任务名称
     *
     * @return
     */
    String value() default "";

    /**
     * 是否支持{@link org.springframework.transaction.annotation.Transactional}
     * 指定 taskBeanName 时无效
     *
     * @return
     */
    boolean transactional() default false;

    /**
     * 显示信息
     *
     * @return
     */
    boolean showInfo() default false;

    /**
     * 根据beanName注入 {@link com.aeuok.task.BindTaskContainerRunnable}
     *
     * @return
     */
    String taskBeanName() default "";

    /**
     * 任务等待
     *
     * @return
     */
    boolean waitTask() default true;
}
