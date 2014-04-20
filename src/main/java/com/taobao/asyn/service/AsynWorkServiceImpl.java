package com.taobao.asyn.service;

import java.lang.reflect.Constructor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.taobao.asyn.spring.AsynWorkSpringUtil;
import com.taobao.asyn.unit.AsynThreadPoolExecutor;
import com.taobao.asyn.unit.AsynWork;
import com.taobao.asyn.unit.AsynWorkUnit;
import com.taobao.asyn.unit.PerfectRunsPolicy;

/**
 * @author junyu.wy
 */
public class AsynWorkServiceImpl implements AsynWorkService {
    // 线程池维护线程的最少数量,CPU个数加1
    private int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() + 1;

    // 线程池维护线程所允许的空闲时间
    private int KEEP_ALIVE_TIME = 1000;

    private Lock lock = new ReentrantLock();// 去掉了静态，所以这个没有用了

    protected AtomicLong allAsynWorkNum = new AtomicLong(0);

    protected AtomicLong executedWorkNum = new AtomicLong(0);

    private final Log log = LogFactory.getLog(AsynWorkServiceImpl.class);

    private String defaultHandlder;

    // 线程池维护线程的最大数量
    private int MAX_POOL_SIZE = 10;

    public ConcurrentHashMap<String, Object> targetCacheMap =
            new ConcurrentHashMap<String, Object>();

    // 高优先级消息队列线程池
    public AsynThreadPoolExecutor threadPool;

    // 线程池所使用的缓冲队列大小
    private int WORK_QUEUE_SIZE = 20;

    private AsynWorkService workService;


    public int getCORE_POOL_SIZE() {
        return CORE_POOL_SIZE;
    }

    public int getKEEP_ALIVE_TIME() {
        return KEEP_ALIVE_TIME;
    }

    public int getMAX_POOL_SIZE() {
        return MAX_POOL_SIZE;
    }

    public int getWORK_QUEUE_SIZE() {
        return WORK_QUEUE_SIZE;
    }

    public void setCORE_POOL_SIZE(int cORE_POOL_SIZE) {
        CORE_POOL_SIZE = cORE_POOL_SIZE;
    }

    public void setKEEP_ALIVE_TIME(int kEEP_ALIVE_TIME) {
        KEEP_ALIVE_TIME = kEEP_ALIVE_TIME;
    }

    public void setMAX_POOL_SIZE(int mAX_POOL_SIZE) {
        MAX_POOL_SIZE = mAX_POOL_SIZE;
    }

    public void setWORK_QUEUE_SIZE(int wORK_QUEUE_SIZE) {
        WORK_QUEUE_SIZE = wORK_QUEUE_SIZE;
    }


    private RejectedExecutionHandler rejectedExecutionHandler;


    public AsynWorkServiceImpl() throws Exception{
        newInstance();
    }

    public AsynWorkServiceImpl(
            int CORE_POOL_SIZE, int MAX_POOL_SIZE, int KEEP_ALIVE_TIME,
            int WORK_QUEUE_SIZE, String defaultHandlder,
            RejectedExecutionHandler rejectedExecutionHandler) throws Exception{
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        this.defaultHandlder = defaultHandlder;
        this.CORE_POOL_SIZE = CORE_POOL_SIZE;
        this.MAX_POOL_SIZE = MAX_POOL_SIZE;
        this.KEEP_ALIVE_TIME = KEEP_ALIVE_TIME;
        this.WORK_QUEUE_SIZE = WORK_QUEUE_SIZE;
        newInstance();
    }

    @Override
    public void addAsynWorkUnit(AsynWorkUnit workUnit) {
        if (workUnit == null) {
            throw new IllegalArgumentException("workUnit is null");
        }
        AsynWork workHandler = new AsynWork(workUnit);
        threadPool.execute(workHandler);
        allAsynWorkNum.incrementAndGet();
    }

    @Override
    public void addAsynWork(Object tagerObject, String method) {
        addAsynWork(tagerObject,method,null);
    }

    @Override
    public void addAsynWork(Object tagerObject, String method, Object[] params) {
        addAsynWork(tagerObject,method,params,false);
    }

    @Override
    public void addAsynWork(Object tagerObject, String method, Object[] params,
            boolean cache) {
        if (tagerObject == null || method == null) {
            throw new IllegalArgumentException(
                    "target name is null or  target method name is null");
        }
        Object target = null;
        if (tagerObject.getClass().isAssignableFrom(String.class)) {
            addAsynWorkWithSpring(tagerObject.toString(),method,params);
            return;
        } else if (tagerObject instanceof Class) {
            String classKey = ((Class<?>) tagerObject).getSimpleName();
            if (cache) {
                target = targetCacheMap.get(classKey);
                if (target == null) {
                    target = newObject((Class<?>) tagerObject);
                    targetCacheMap.put(classKey,target);
                }
            } else {
                target = newObject((Class<?>) tagerObject);
            }
        } else {
            target = tagerObject;
        }
        if (target == null) {
            throw new IllegalArgumentException("target object is null");
        }
        AsynWorkUnit workUnit = new AsynWorkUnit(target,method,params);
        addAsynWorkUnit(workUnit);
    }

    @Override
    public void addAsynWorkWithSpring(String target, String method, Object[] params) {
        if (target == null || method == null) {
            throw new IllegalArgumentException(
                    "target name is null or  target method name is null");
        }
        Object bean = AsynWorkSpringUtil.getBean(target);
        if (bean == null) {
            throw new IllegalArgumentException("spring bean is null");
        }
        AsynWorkUnit workUnit = new AsynWorkUnit(bean,method,params);
        addAsynWorkUnit(workUnit);
    }

    @Override
    public void addAsynWork(MethodInvocation invocation) {
        AsynWorkUnit workUnit = new AsynWorkUnit(invocation);
        addAsynWorkUnit(workUnit);
    }
    
    @Override
    public void close() {
        threadPool.shutdown();
    }

    @Override
    public void close(long waitTime) {
        threadPool.shutdown();
    }

    private void newInstance() throws Exception {
        if (threadPool == null) {
            if (rejectedExecutionHandler == null) {
                if (defaultHandlder == null) {
                    rejectedExecutionHandler = new PerfectRunsPolicy();
                } else if (defaultHandlder.equals("CallerRunsPolicy")) {
                    rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                } else if (defaultHandlder.equals("AbortPolicy")) {
                    rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                } else if (defaultHandlder.equals("DiscardOldestPolicy")) {
                    rejectedExecutionHandler =
                            new ThreadPoolExecutor.DiscardOldestPolicy();
                } else if (defaultHandlder.equals("DiscardPolicy")) {
                    rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                } else {
                    throw new Exception("not finding your rejectedExecutionHandler");
                }
            }
            threadPool =
                    new AsynThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,
                            KEEP_ALIVE_TIME,TimeUnit.MILLISECONDS,
                            new ArrayBlockingQueue<Runnable>(WORK_QUEUE_SIZE),
                            rejectedExecutionHandler,executedWorkNum);
        }
    }

   
    /**
      * @Description: TODO
      * @param clzss
      * @return
      * @author ASUS
      */
    private Object newObject(Class<?> clzss) {
        try {
            Constructor<?> constructor = clzss.getConstructor();
            if (constructor == null) {
                throw new IllegalArgumentException(
                        "target not have default constructor function");
            }
            return clzss.newInstance();
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     *此方法只用于当所有异步工作全部提交完毕时使用
     *@param waitTime
     */
    @Override
    public void waitAllAsynWorksCompleted(Integer waitTime) {
        while (true) {
            if (allAsynWorkNum.get() == executedWorkNum.get()) {
                break;
            } else {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public Long getAllAsynWorksNum() {
        return allAsynWorkNum.get();
    }

    @Override
    public Long getExecutedAsynWorksNum() {
        return executedWorkNum.get();
    }

}
