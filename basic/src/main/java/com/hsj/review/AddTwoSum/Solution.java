package com.hsj.review.AddTwoSum;

/**
 * Created by hanhansongjiang on 17/5/31.
 */
class ListNode {
    int val;

    ListNode next;

    ListNode(int x) {
        this.val = x;
    }


}

public class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        int jinwei = 0;

        ListNode head = new ListNode(-1);//链表结果的头节点

        ListNode tail = head;//链表结果的头节点


        while (l1 != null || l2 != null) {

            ListNode listNode = new ListNode(-1);

            int sum=0;
            if(l1!=null)  sum+=l1.val;
            if(l2!=null)  sum+=l2.val;


            listNode.val =( sum+jinwei) % 10;

            jinwei = (sum+jinwei) / 10;


            tail.next = listNode;

            tail = listNode;
            if(l1!=null) l1=l1.next;
            if(l2!=null) l2=l2.next;



        }

        return head.next;
    }

    public static void main(String args[]) {

        Solution solution = new Solution();
        ListNode listNode1 = new ListNode(2);
        ListNode listNode2 = new ListNode(4);
        ListNode listNode3 = new ListNode(3);
        listNode1.next = listNode2;
        listNode2.next = listNode3;

        ListNode listNode12 = new ListNode(5);
        ListNode listNode22 = new ListNode(6);
        ListNode listNode33 = new ListNode(4);
        listNode12.next = listNode22;
        listNode22.next = listNode33;

        ListNode ans = solution.addTwoNumbers(listNode1, listNode12);

        while (ans != null) {

            System.out.println(ans.val);
            ans = ans.next;
        }


    }
}