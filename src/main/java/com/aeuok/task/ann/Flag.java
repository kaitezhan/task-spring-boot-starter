package com.aeuok.task.ann;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Flag {
    Class<?> value();
}
