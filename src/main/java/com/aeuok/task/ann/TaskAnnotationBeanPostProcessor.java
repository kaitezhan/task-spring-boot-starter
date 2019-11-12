package com.aeuok.task.ann;

import com.aeuok.task.TaskContainer;
import com.aeuok.task.TaskRunnable;
import com.aeuok.task.TransactionalTaskRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author: CQ
 */
public class TaskAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(TaskAnnotationBeanPostProcessor.class);
    private BeanFactory beanFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = AopUtils.getTargetClass(bean).getDeclaredFields();
        Task task;
        for (Field field : fields) {
            task = field.getAnnotation(Task.class);
            if (null != task) {
                if (log.isInfoEnabled()) {
                    log.info("{} 开始注入任务容器", bean.getClass().getName());
                }
                TaskContainer taskContainer = beanFactory.getBean(TaskContainer.class);
                taskContainer.setShowInfo(task.showInfo());
                taskContainer.setTaskName(task.value().length() == 0 ? bean.getClass().getName() : task.value());
                if (task.transactional()) {
                    taskContainer.setObjectProvider(beanFactory.getBeanProvider(TransactionalTaskRunnable.class));
                } else {
                    taskContainer.setObjectProvider(beanFactory.getBeanProvider(TaskRunnable.class));
                }
                field.setAccessible(true);
                try {
                    field.set(bean, taskContainer);
                } catch (IllegalAccessException e) {
                    log.error("注入任务容器失败\n{}", e.getMessage());
                }
            }
        }
        return bean;
    }


    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
