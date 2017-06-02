package com.hsj.review.twoSum;

import java.util.Arrays;

/**
 * Created by hanhansongjiang on 17/5/31.
 */


class Point implements Comparable<Point>
{
     int x;

     int y;

    public Point(int x,int y){
        this.x=x;
        this.y=y;
    }

    public int compareTo(Point o) {
        return  x-o.x;
    }
}
public class TwoSUm {
    public  static  int[] twoSum(int[] numbers, int target) {

        int ans[] = new int[2];

        Point[] points=new Point[numbers.length];

        for(int i=0;i<numbers.length;i++){

            Point point=new Point(numbers[i],i);
            points[i]=point;

        }

        Arrays.sort(points);


        int begin=0;
        int end=numbers.length-1;

        while(begin<end){


            int sum=points[begin].x+points[end].x;

            if(sum==target){
                ans[0]=points[begin].y;
                ans[1]=points[begin].x;
                break;
            }else if(sum>target){

                end--;
            }else{
                begin++;
            }

        }



        return ans;

    }



    public static void main(String args[]){

        int ans[]=twoSum(new int[]{3,2,4},6);
        System.out.print(ans[0]+" "+ans[1]);

    }
}