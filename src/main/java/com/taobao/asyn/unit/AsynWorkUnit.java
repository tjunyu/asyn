package com.taobao.asyn.unit;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInvocation;

import com.taobao.asyn.util.AsynUtil;

/**
 * @author junyu.wy
 */
public class AsynWorkUnit {
    // method Cache Map
    private final static Map<String, Method> methodCacheMap =
            new ConcurrentHashMap<String, Method>();

    private int target;// 1.是通用 2.spring

    private String method;

    private Object[] params;

    private Object targetObject;

    private MethodInvocation invocation;


    public AsynWorkUnit( MethodInvocation invocation){
        this.invocation = invocation;
        target = 2;
    }

    public AsynWorkUnit( Object target, String method){
        this(target,method,null);
    }

    public AsynWorkUnit( Object target, String method, Object[] params){
        if (target == null || method == null) {
            throw new IllegalArgumentException("target or method  is null");
        }
        this.targetObject = target;
        this.method = method;
        this.params = params;
        this.target = 1;
    }

    public void call() throws Throwable {
        if (target == 1) {
            if (targetObject == null) {
                throw new RuntimeException("target object is null");
            }
            Class<?> clazz = targetObject.getClass();
            String methodKey = AsynUtil.getClassMethodKey(clazz,params,method);
            Method targetMethod = methodCacheMap.get(methodKey);
            if (targetMethod == null) {
                targetMethod = AsynUtil.getTargetMethod(clazz,params,method);
                if (targetMethod != null) {
                    methodCacheMap.put(methodKey,targetMethod);
                }
            }
            if (targetMethod == null) {
                throw new IllegalArgumentException("targetObject method is null");
            }
            targetMethod.invoke(targetObject,params);
        } else if (target == 2) {
            if (invocation == null) {
                throw new RuntimeException("invocation object is null");
            }
            invocation.proceed();
        }
    }

    public String getThreadName() {
        if (target == 1) {
            String className = targetObject.getClass().getSimpleName();
            StringBuilder sb = new StringBuilder();
            sb.append(className).append("-").append(method);
            return sb.toString();
        } else if (target == 2) {
            return invocation.getClass().getSimpleName()+invocation.getMethod().getName();
        }else{
            return "";
        }
    }
}
