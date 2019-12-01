package com.aeuok.task.runnable;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author: CQ
 */
public interface TransactionalTaskRunnable extends BindTaskContainerRunnable {
    /**
     * run
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    void run();
}
