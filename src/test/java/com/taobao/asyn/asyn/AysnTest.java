package com.taobao.asyn.asyn;

import java.util.Date;

import org.junit.Test;

import com.taobao.asyn.TestCase;
import com.taobao.asyn.service.AsynWorkServiceImpl;
import com.taobao.itest.annotation.ITestSpringBean;


public class AysnTest extends BaseCaseTest{
    
    @ITestSpringBean    
    private TestCase testCase;
    
//    @ITestSpringBean
//    private AsynWorkServiceImpl asynWorkService = new AsynWorkServiceImpl();
    
    @Test
    public void testA() {
        try { //出现没有调试到的现象是当前主线程执行完毕了，子线程还没有执行就结束了，所以会出现像没有执行的效果，对于这种全局代理的
            AsynWorkServiceImpl asynWorkService = new AsynWorkServiceImpl();
//            Thread a = new Thread(){
//
//                @Override
//                public void run() {
//                    asynWorkService.waitAllAsynWorksCompleted(3000);
//                    System.out.println("aaaaaaaaaaaaaaaaaaa");
//                }
//                
//            };
//            a.start();
            System.out.println("主线程id="+Thread.currentThread().getId());
            int i = 0;
            while(i<100){
//                asynWorkService.addAsynWork(TestCase.class,"sayThread");
                testCase.sayThread();
                i++;
            }
            System.out.println("開始等待，time"+new Date()+"   總數："+asynWorkService.getAllAsynWorksNum()+"   已經執行："+asynWorkService.getExecutedAsynWorksNum());
            asynWorkService.waitAllAsynWorksCompleted(3000);
            System.out.println("等待結束，time"+new Date()+"   總數："+asynWorkService.getAllAsynWorksNum()+"   已經執行："+asynWorkService.getExecutedAsynWorksNum());
            asynWorkService = null;
            while (true) {
                        Thread.sleep(3000);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
