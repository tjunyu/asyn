package com.taobao.asyn.unit;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
/**
 * 解决不调用住线程不调用执行前后的方法
 * @author junyu.wy
 */
public class PerfectRunsPolicy implements RejectedExecutionHandler{

    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadpoolexecutor)
    {
        if(!threadpoolexecutor.isShutdown()){
            AsynThreadPoolExecutor asynThreadPoolExecutor = (AsynThreadPoolExecutor) threadpoolexecutor;
            runnable.run();
            asynThreadPoolExecutor.afterExecute(runnable,null);
        }
    }
    
    public PerfectRunsPolicy(){
        
    }
    
}
