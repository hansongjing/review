package com.hsj.con;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by hanhansongjiang on 17/6/7.
 */
public class DeadLock {


    public static void main(String args[]) {

        Object lock1 = new Object();

        Object lock2 = new Object();

        Executor executors = Executors.newFixedThreadPool(10);
        A a=new A(lock1,lock2);

        A a1=new A(lock2,lock1);

        executors.execute(a);
        executors.execute(a1);




    }


}

class A implements Runnable{
    private Object lock1;

    private Object lock2;

    public A(Object lock1,Object lock2){
        this.lock1=lock1;
        this.lock2=lock2;

    }
    public void run() {
        synchronized (lock1){

            sleep3000();

            synchronized (lock2){

                System.out.println("得到两把锁");
            }

        }

    }

    private void sleep3000(){

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


