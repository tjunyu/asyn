package com.taobao.asyn.spring;

import java.util.concurrent.RejectedExecutionHandler;

import org.springframework.beans.factory.FactoryBean;

import com.taobao.asyn.service.AsynWorkService;
import com.taobao.asyn.service.AsynWorkServiceImpl;
/**
 * @author junyu.wy
 */
public class AsynServiceFactoryBean implements FactoryBean {

    private  int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;
    
    private  int KEEP_ALIVE_TIME = 1000;
    
    private  int MAX_POOL_SIZE = 10;
    
    private  int WORK_QUEUE_SIZE = 20;
    
    private RejectedExecutionHandler rejectedExecutionHandler;
    
    private String defaultHandlder;
    
    @Override
    public Object getObject() throws Exception {
        AsynWorkService asynService = new AsynWorkServiceImpl(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,WORK_QUEUE_SIZE,defaultHandlder,rejectedExecutionHandler);
        return asynService;
    }

    @Override
    public Class<?> getObjectType() {
        return AsynWorkService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public RejectedExecutionHandler getRejectedExecutionHandler() {
        return rejectedExecutionHandler;
    }

    public void
            setRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    public String getDefaultHandlder() {
        return defaultHandlder;
    }

    public void setDefaultHandlder(String defaultHandlder) {
        this.defaultHandlder = defaultHandlder;
    }

    public  int getCORE_POOL_SIZE() {
        return CORE_POOL_SIZE;
    }

    public  void setCORE_POOL_SIZE(int cORE_POOL_SIZE) {
        CORE_POOL_SIZE = cORE_POOL_SIZE;
    }

    public  int getKEEP_ALIVE_TIME() {
        return KEEP_ALIVE_TIME;
    }

    public  void setKEEP_ALIVE_TIME(int kEEP_ALIVE_TIME) {
        KEEP_ALIVE_TIME = kEEP_ALIVE_TIME;
    }

    public  int getMAX_POOL_SIZE() {
        return MAX_POOL_SIZE;
    }

    public  void setMAX_POOL_SIZE(int mAX_POOL_SIZE) {
        MAX_POOL_SIZE = mAX_POOL_SIZE;
    }

    public  int getWORK_QUEUE_SIZE() {
        return WORK_QUEUE_SIZE;
    }

    public  void setWORK_QUEUE_SIZE(int wORK_QUEUE_SIZE) {
        WORK_QUEUE_SIZE = wORK_QUEUE_SIZE;
    }

}
