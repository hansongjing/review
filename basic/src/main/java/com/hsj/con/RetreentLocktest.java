package com.hsj.con;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hanhansongjiang on 17/6/7.
 */
public class RetreentLocktest {

    public static void main(String args[]) throws InterruptedException {

        final BlockQueue blockQueue=new BlockQueue();

        Executor executor= Executors.newFixedThreadPool(10);

       Runnable runnable=new Runnable() {
           public void run() {

               for(int i=0;i<15;i++){

                   try {
                       blockQueue.put(i+"");
                       System.out.println(blockQueue.length());
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }

           }
       };

       executor.execute(runnable);





    }


}


class BlockQueue {

    private LinkedList<String> linkedList;

    private Condition fullCondition;

    private Condition emptyConditon;


    private Lock lock;


    public BlockQueue() {

        linkedList = new LinkedList<String>();
        lock = new ReentrantLock();

        fullCondition = lock.newCondition();
        emptyConditon = lock.newCondition();

    }


    public void put(String s) throws InterruptedException {

        if (linkedList.size() == 10) {

            fullCondition.await();

        }

        linkedList.offer(s);

        emptyConditon.signal();



    }

    public void get()  throws InterruptedException{

        if (linkedList.size() == 0) {
            emptyConditon.await();
            ;
        }

        linkedList.poll();

        fullCondition.signal();

    }

    public int length(){
        return linkedList.size();
    }



}
