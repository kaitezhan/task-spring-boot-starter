package com.aeuok.task.ann;

import com.aeuok.task.TaskContainer;
import com.aeuok.task.TaskRunnable;
import com.aeuok.task.TransactionalTaskRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AdvisedSupport;
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
        Class<?> targetClass = bean.getClass();
        Field[] fields = targetClass.getDeclaredFields();
        Task task;
        for (Field field : fields) {
            if (!TaskContainer.class.isAssignableFrom(field.getType())) {
                continue;
            }
            task = field.getAnnotation(Task.class);
            if (null == task) {
                continue;
            }
            TaskContainer taskContainer = beanFactory.getBean(TaskContainer.class);
            taskContainer.setShowInfo(task.showInfo());
            taskContainer.setTaskName(task.value().length() == 0 ? targetClass.getName() : task.value());
            if (task.transactional()) {
                taskContainer.setObjectProvider(beanFactory.getBeanProvider(TransactionalTaskRunnable.class));
            } else {
                taskContainer.setObjectProvider(beanFactory.getBeanProvider(TaskRunnable.class));
            }
            field.setAccessible(true);
            try {
                field.set(bean, taskContainer);
            } catch (IllegalAccessException e) {
                log.error("{} 注入任务容器失败\n{}", targetClass.getName(), e.getMessage());
            }
        }
        return bean;
    }

    /**
     * 获取最终对象
     *
     * @param source
     * @return
     * @throws Exception
     */
    private Object getTargetObject(Object source) throws Exception {
        Object target;
        if (AopUtils.isJdkDynamicProxy(source)) {
            target = getProxyTargetObject(source, false);
            return getTargetObject(target);
        }
        if (AopUtils.isCglibProxy(source)) {
            target = getProxyTargetObject(source, true);
            return getTargetObject(target);
        }
        return source;
    }

    /**
     * 获取代理的对象
     *
     * @param proxy
     * @param isCglib
     * @return
     */
    private Object getProxyTargetObject(Object proxy, boolean isCglib) throws Exception {
        Field targetField;
        if (isCglib) {
            targetField = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        } else {
            targetField = proxy.getClass().getSuperclass().getDeclaredField("h");
        }
        targetField.setAccessible(true);
        Object obj = targetField.get(proxy);
        Field advised = obj.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport) advised.get(obj)).getTargetSource().getTarget();
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
