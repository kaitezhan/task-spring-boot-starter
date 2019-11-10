package com.aeuok.task.starter;

import com.aeuok.task.TaskContainer;
import com.aeuok.task.TaskContainerGenerator;
import com.aeuok.task.TaskRunnable;
import com.aeuok.task.TransactionalTaskRunnable;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        if (properties.isEnableDefaultTransactional()) {
            return new TransactionalTaskRunnable();
        } else {
            //TODO
            return null;
        }
    }

    @Bean
    @Scope("prototype")
    public TaskContainer taskContainer(ObjectFactory<TaskRunnable> objectFactory) {
        TaskContainer taskContainer = new TaskContainer();
        taskContainer.setRunnableFactory(objectFactory);
        taskContainer.setDebugger(properties.isDebugger());
        return taskContainer;
    }

    @Bean
    public TaskContainerGenerator taskContainerGenerator(ObjectFactory<TaskContainer> objectFactory) {
        TaskContainerGenerator taskContainerGenerator = new TaskContainerGenerator();
        taskContainerGenerator.setFactory(objectFactory);
        return taskContainerGenerator;
    }
}
