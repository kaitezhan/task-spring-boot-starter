package com.aeuok.task.ann;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * @author: CQ
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Task {
    /**
     * 任务名称
     *
     * @return
     */
    String value() default "";


    boolean transactional() default false;

    /**
     * 显示信息
     *
     * @return
     */
    boolean showInfo() default false;
}
