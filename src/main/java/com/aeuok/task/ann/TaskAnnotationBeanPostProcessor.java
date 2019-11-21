package com.aeuok.task.ann;

import com.aeuok.task.Constant;
import com.aeuok.task.TaskContainer;
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
    private String taskNamePrefix;

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
        if (log.isDebugEnabled()) {
            log.debug("正在为【{}】注入 TaskContainer");
        }
        for (int index = Constant.TASK_FIELD_INJECT_START; ; index++) {
            try {
                Method injectMethod = realClass.getDeclaredMethod(Constant.TASK_FIELD_INJECT_PREFIX + index, TaskContainer.class);
                TaskContainer taskContainer = beanFactory.getBean(TaskContainer.class);
                taskContainer.setTaskNamePrefix(taskNamePrefix);
                //TODO 这里要在处理注解时注入
                taskContainer.setTransactional(true);
                injectMethod.invoke(bean, taskContainer);
            } catch (NoSuchMethodException e) {
                break;
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return bean;
    }

    public void setTaskNamePrefix(String taskNamePrefix) {
        this.taskNamePrefix = taskNamePrefix;
    }
}
