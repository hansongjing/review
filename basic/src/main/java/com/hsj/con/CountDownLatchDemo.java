package com.hsj.con;

import com.hsj.con.proxy.Count;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by hanhansongjiang on 17/6/12.
 * 模拟赛跑，所有队员准备好后才能开始，所有人到达后才能计算成绩
 */
public class CountDownLatchDemo {


    public static void main(String args[]) {


        CountDownLatch beginLatch = new CountDownLatch(1);

        CountDownLatch endLatch = new CountDownLatch(3);


        Executor executor = Executors.newCachedThreadPool();
        for(int i=0;i<3;i++) {
            executor.execute(new Runner(beginLatch, endLatch));
        }

//        beginLatch.countDown();

        try {
            beginLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}

class Runner implements Runnable {

    private CountDownLatch beginLatch;

    private CountDownLatch endLatch;


    public Runner(CountDownLatch beginLatch, CountDownLatch endLatch) {
        this.beginLatch = beginLatch;
        this.endLatch = endLatch;
    }

    public void run() {
        try {
            beginLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getId() + "到达终点");

        endLatch.countDown();


    }
}
