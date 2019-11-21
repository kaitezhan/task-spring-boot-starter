package com.aeuok.task.ann;

import java.lang.annotation.*;

/**
 * @author: CQ
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Flag {
    /**
     * 原始类
     *
     * @return
     */
    Class<?> value();
}
