package com.taobao.asyn.asyn;

import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;

import java.lang.reflect.Proxy;

import java.util.Date;

 

interface Hello {

       void sayHello(String name);

}



 class MyHello implements Hello {

      public void sayHello(String name) {

            System.out.println("Hello " + name);

        }







enum Level {

  INFO,DEBUGE;

}



static class Logger {

  public static void logging(Level level, String context) {

      if (level.equals(Level.INFO)) {

          System.out.println(new Date()+ " " + context);

      }

      if (level.equals(Level.DEBUGE)) {

          System.out.println(new Date() + " " + context);

      }

  }

}

}


public class TestProxy {
    public static void main(String[] args) {

        MyHello hello = (MyHello)new DynaProxyHello().bind(new MyHello());

       hello.sayHello(" Spring AOP");

   }

}
