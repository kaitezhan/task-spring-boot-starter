package com.aeuok.task.starter;

import com.aeuok.task.*;
import com.aeuok.task.ann.TaskAnnotationBeanPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author: CQ
 */
@AutoConfigureOrder(-99)
@Configuration
@EnableConfigurationProperties(TaskProperties.class)
public class TaskAutoConfigure {
    @Autowired
    private TaskProperties properties;


    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean(TaskRunnable.class)
    public BindTaskContainerRunnable taskRunnable() {
        return null;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean(TransactionalTaskRunnable.class)
    public BindTaskContainerRunnable transactionalTaskRunnable() {
        return new DefaultTransactionalTaskRunnable();
    }

    @Bean
    @Scope("prototype")
    public DefaultTaskContainer taskContainer() {
        return new DefaultTaskContainer();
    }

    @Bean
    public TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor(ConfigurableBeanFactory configurableBeanFactory,
                                                                           BeanFactory beanFactory) {
        TaskAnnotationBeanPostProcessor processor = new TaskAnnotationBeanPostProcessor();
        processor.setBeanFactory(beanFactory);
        configurableBeanFactory.addBeanPostProcessor(processor);
        return processor;
    }

}
