# task-spring-boot-starter

## `多线程任务处理工具`
### Maven
    <dependency>
        <groupId>com.aeuok</groupId>
        <artifactId>task-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </dependency>
### `@Task` 注解

| 属性| 默认值 |  描述  |
| ---| -----|----|
| name| ""|任务名称,默认 {className}@{filedName}|
| transactional|false|是否支持org.springframework.transaction.annotation.Transactional|
| showInfo|false|是否显示信息|
| taskBeanName|""|不为空时根据beanName注入 com.aeuok.task.runnable.BindTaskContainerRunnable|
| waitTask|true|是否任务间互相等待|
### `TaskDefinition`
        /**
         * 任务内容
         *
         * @return 执行结果 true-成功
         * @throws Exception
         */
        boolean task() throws Exception;
### `TaskContainerFactory` 任务容器工厂
        public TaskContainer get();
        public TaskContainer get(TaskDefinition task);
        public TaskContainer get(List<TaskDefinition> tasks);
### 示例
        @Task
        private TaskContainerFactory factory;

        public void test() {
            List<TaskDefinition> list = new ArrayList<>();
            list.add(() -> {
                System.out.println("test 1");
                return true;
            });
            list.add(() -> {
                System.out.println("test 2");
                return true;
            });
            factory.get(list).execute();
        }