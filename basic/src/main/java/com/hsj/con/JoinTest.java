package com.hsj.con;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by hanhansongjiang on 17/6/12.
 */
public class JoinTest {

    public static void main(String args[]){

        Executor executor= Executors.newSingleThreadExecutor();
        int count=0;
        while(count<100){
            executor.execute(new Thread_1(count));
            count++;
        }



    }
}

class Thread_1 implements Runnable{

    private  int i;

    public Thread_1(int i) {
        this.i=i;
    }


    public void run() {

        System.out.println(i++);

    }
}
