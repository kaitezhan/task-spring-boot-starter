package com.aeuok.task.core;

import com.aeuok.task.*;
import org.springframework.beans.factory.BeanFactory;
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

    @Bean(Constant.DEFAULT_TASK_FACTORY_BEAN_NAME)
    @Scope("prototype")
    public TaskContainerFactory taskFactory(BeanFactory beanFactory) {
        return new TaskContainerFactory(beanFactory);
    }

    @Bean
    public TaskAnnotationBeanPostProcessor taskAnnotationBeanPostProcessor(ConfigurableBeanFactory configurableBeanFactory,
                                                                           BeanFactory beanFactory) {
        TaskAnnotationBeanPostProcessor processor = new TaskAnnotationBeanPostProcessor(beanFactory);
        configurableBeanFactory.addBeanPostProcessor(processor);
        return processor;
    }

}
