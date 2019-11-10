package com.aeuok.task;

/**
 * @author: CQ
 */
@FunctionalInterface
public interface TaskDefinition {
    /**
     * 任务内容
     *
     * @return 执行结果 true-成功
     * @throws Exception
     */
    boolean task() throws Exception;
}
