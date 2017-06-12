package com.hsj.con.proxy;

/**
 * Created by hanhansongjiang on 17/6/12.
 */
public class Count implements CountInterface {

    public int getCount() {
        return count;
    }

    private int count;

    public Count(int count){
        this.count=count;
    }
    public int count() {
        return count+1;
    }


}
