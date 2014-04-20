package com.taobao.asyn.spring;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeansException;

import com.taobao.asyn.service.AsynWorkService;
import com.taobao.asyn.service.AsynWorkServiceImpl;
/**
 * @author junyu.wy
 */
public class AsynWorkHandler extends AbstractAutoProxyCreator{
    
    private static final long serialVersionUID = 1L;
    private AsynWorkService asynWorkService;
    private Map<String,String> asynMap = new HashMap<String,String>();
    private Set<String> asynSet = new HashSet<String>();
    @Override
    protected Object[] getAdvicesAndAdvisorsForBean(Class beanClass, String beanName,
            TargetSource customTargetSource) throws BeansException {
        String methods = asynMap.get(beanClass.getName());
        if(methods!=null){
            String[] methodz = methods.split(",");
            for(String method:methodz){
                asynSet.add(beanClass.getName()+"."+method);
            }
            this.setProxyTargetClass(true);
            return new AsynWorkAdvisor[]{new AsynWorkAdvisor()};
        }
        return null;
    }
    
    class AsynWorkAdvisor implements Advisor{
        @Override
        public Advice getAdvice() {
            return new AsynWorkAdvice();
        }

        @Override
        public boolean isPerInstance() {
            return false;
        }
    }
    
    class AsynWorkAdvice implements MethodInterceptor, Advice{

        @Override
        public Object invoke(final MethodInvocation invocation) throws Throwable {
            
            ;
            
            if(asynWorkService==null)
                asynWorkService = new AsynWorkServiceImpl();
            String methodName =invocation.getThis().getClass().getName() + "."+ invocation.getMethod().getName(); 
            if(asynSet.contains(methodName)){
                asynWorkService.addAsynWork(invocation);
//                Thread a = new Thread(){
//
//                    @Override
//                    public void run() {
//                        try {
//                            System.out.println("aaa");
//                            invocation.proceed();
//                        } catch (Throwable e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                    
//                };
//                a.start();
                return null;
            }else
                return invocation.proceed();
        }
        
    }
    

    public AsynWorkService getAsynWorkService() {
        return asynWorkService;
    }

    public void setAsynWorkService(AsynWorkService asynWorkService) {
        this.asynWorkService = asynWorkService;
    }

    public Map<String, String> getAsynMap() {
        return asynMap;
    }

    public void setAsynMap(Map<String, String> asynMap) {
        this.asynMap = asynMap;
    }
    
}
