package com.taobao.asyn.service;

import org.aopalliance.intercept.MethodInvocation;

import com.taobao.asyn.unit.AsynWorkUnit;

/**
 * @author junyu.wy
 */
public interface AsynWorkService {
    
    public void addAsynWork(MethodInvocation invocation);

    public void addAsynWorkUnit(AsynWorkUnit workUnit);

    public void addAsynWork(Object tagerObject, String method);

    public void addAsynWork(Object tagerObject, String method, Object[] params);

    public void
    addAsynWork(Object tagerObject, String method, Object[] params, boolean cache);

    public void addAsynWorkWithSpring(String target, String method, Object[] params);

    public void close();

    public void close(long waitTime);
    
    public void waitAllAsynWorksCompleted(Integer waitTime);
    
    public Long getAllAsynWorksNum();
    
    public Long getExecutedAsynWorksNum();
}
