package com.siemens.dasheng.web.singleton;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author xuxin
 * SieExecutors
 * created by xuxin on 15/12/2018
 */
public class SieExecutorsPool {

    private static final int DEFAULT_CAPACITY = 30;

    private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();
    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(DEFAULT_CAPACITY, DEFAULT_CAPACITY,600,
            TimeUnit.SECONDS,new LinkedBlockingQueue(), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 执行任务
     * @param task
     */
    public static void executor(Runnable task) {
        EXECUTOR.execute(task);
    }





}
