package com.aeuok.task.starter;

import com.aeuok.task.*;
import com.aeuok.task.ann.TaskAnnotationBeanPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author: CQ
 */
@Configuration
@EnableConfigurationProperties(TaskProperties.class)
public class TaskAutoConfigure {

    @Autowired
    private TaskProperties properties;

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean(TaskRunnable.class)
    public TaskRunnable taskRunnable() {
        return new DefaultTaskRunnable();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean(TransactionalTaskRunnable.class)
    public TransactionalTaskRunnable transactionalTaskRunnable() {
        return new DefaultTransactionalTaskRunnable();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean(TaskContainer.class)
    public TaskContainer taskContainer(BeanFactory beanFactory) {
        return new TaskContainer(beanFactory);
    }

    @Bean
    public TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor(ConfigurableBeanFactory configurableBeanFactory,
                                                                           BeanFactory beanFactory) {
        TaskAnnotationBeanPostProcessor processor = new TaskAnnotationBeanPostProcessor(beanFactory);
        processor.setTaskNamePrefix(properties.getTaskNamePrefix());
        configurableBeanFactory.addBeanPostProcessor(processor);
        return processor;
    }

}
