package com.taobao.asyn;

public class TestCase implements TestCaseInter{
    public void sayThread() throws Exception{
        System.out.println("当前执行线程：="+Thread.currentThread().getId());
//        throw new Exception("test");
        Thread.sleep(1000);
    }
}
