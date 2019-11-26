package com.aeuok.task.ann;

import com.aeuok.task.Constant;
import com.aeuok.task.TaskFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * @author: CQ
 */
public class TaskAnnotationBeanPostProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(TaskAnnotationBeanPostProcessor.class);
    private BeanFactory beanFactory;

    public TaskAnnotationBeanPostProcessor(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Flag flag = bean.getClass().getAnnotation(Flag.class);
        if (null == flag) {
            return bean;
        }
        Class<?> realClass = flag.value();
        log.info("正在为【{}】注入 TaskFactory", realClass.getName());
        for (int index = Constant.TASK_FIELD_INJECT_START; ; index++) {
            try {
                Method injectMethod = realClass.getDeclaredMethod(Constant.TASK_FIELD_INJECT_PREFIX + index, TaskFactory.class);
                TaskFactory taskFactory = beanFactory.getBean(Constant.DEFAULT_TASK_FACTORY_BEAN_NAME, TaskFactory.class);
                injectMethod.invoke(bean, taskFactory);
            } catch (NoSuchMethodException e) {
                break;
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
