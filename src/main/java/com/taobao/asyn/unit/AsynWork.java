package com.taobao.asyn.unit;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author junyu.wy
 */
public class AsynWork implements Serializable , Runnable {
    private static final Log log = LogFactory.getLog(AsynWork.class);

    /**
     * @Fields serialVersionUID:
     */
    private static final long serialVersionUID = 1L;

    private AsynWorkUnit workUnit;


    public AsynWork( AsynWorkUnit workUnit){
        this.workUnit = workUnit;
    }

    public AsynWorkUnit getworkUnit() {
        return workUnit;
    }

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        if (workUnit.getThreadName() != null) {
            setName(currentThread,workUnit.getThreadName());
        }
        try {
            workUnit.call();
        } catch (Throwable throwable) {
        }
    }

    /**
     * @param thread
     * @param name
     */
    private void setName(Thread thread, String name) {
        try {
            thread.setName(name);
        } catch (SecurityException se) {
            log.error(se);
        }
    }
}
