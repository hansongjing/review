package com.hsj.con;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hanhansongjiang on 17/6/3.
 */

//打印奇数的线程
class Thread2 implements Runnable {

    private Lock lock;

    public Thread2(Lock lock) {
        this.lock = lock;
    }


    public void run() {
        while (lock.i <  100000) {
            synchronized (lock) {
                if (lock.i % 2 != 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                lock.i++;
                System.out.println(Thread.currentThread()+" "+lock.i);
                lock.notify();


            }

        }
    }
}

//打印奇数的线程
class Thread3 implements Runnable {

    private Lock lock;

    public Thread3(Lock lock) {
        this.lock = lock;
    }


    public void run() {
        while (lock.i < 100000) {
            synchronized (lock) {
                if (lock.i % 2 == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                lock.i++;
                System.out.println(Thread.currentThread()+" "+lock.i);
                lock.notify();


            }

        }
    }
}

//锁
class Lock {
    int i;

    public Lock(int i) {
        this.i = i;
    }
}


public class Thread1 {

    public static void main(String args[]) {
        Lock lock = new Lock(0);

        Thread2 thread2 = new Thread2(lock);
        Thread3 thread3 = new Thread3(lock);

        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

        fixedThreadPool.execute(thread2);
        fixedThreadPool.execute(thread3);


    }


}
