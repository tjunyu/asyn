package com.taobao.asyn.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author junyu.wy
 */
public class AsynWorkSpringUtil implements ApplicationContextAware {
    private static ApplicationContext context = null;

    public static <T> T getBean(Class<T> object, String beanName) {
        return (T) context.getBean(beanName);
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        context = arg0;
    }
}
