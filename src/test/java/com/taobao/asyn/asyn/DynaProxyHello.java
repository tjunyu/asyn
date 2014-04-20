package com.taobao.asyn.asyn;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.taobao.asyn.asyn.MyHello.Level;
import com.taobao.asyn.asyn.MyHello.Logger;

public class DynaProxyHello implements InvocationHandler {

        

        private Object delegate;



        public Object bind(Object delegate) {

            this.delegate = delegate;

            return Proxy.newProxyInstance(this.delegate.getClass().getClassLoader(), this.delegate.getClass().getInterfaces(), this);

        }

         

         public Object invoke(Object proxy, Method method, Object[] args)

                 throws Throwable {

             Object result = null;

             try {

                 //执行原来的方法之前记录日志

                 Logger.logging(Level.INFO, method.getName() + " Method Start!");

                 result = method.invoke(this.delegate, args);

              //执行原来的方法之后记录日志

                 Logger.logging(Level.DEBUGE, method.getName() + " Method end  .");

            } catch (Exception e) {

                 e.printStackTrace();

           }

             return result;

        }

    }
