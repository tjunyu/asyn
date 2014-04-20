package com.taobao.asyn.unit;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author junyu.wy
 */

public class AsynThreadPoolExecutor extends ThreadPoolExecutor {

    private AtomicLong executeWorkNum;
    
    public AsynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                  RejectedExecutionHandler handler, AtomicLong executeWorkNum) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.executeWorkNum = executeWorkNum;
    }

    public AsynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue,RejectedExecutionHandler handler,
                                  AtomicLong executeWorkNum) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
        
        this.executeWorkNum = executeWorkNum;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        executeWorkNum.incrementAndGet();
//        if(Thread.currentThread().getId()==1)
//        System.out.println("after:"+Thread.currentThread().getId()+"aa   num="+executeWorkNum.get());
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
    }

    @Override
    protected void terminated() {
        super.terminated();
    }

    public void afterExecuted(Runnable r, Throwable t) {
        afterExecute(r,t);
    }
    
}
