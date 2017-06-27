package com.hsj.review;

import io.atomix.Atomix;
import io.atomix.AtomixClient;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.group.DistributedGroup;
import io.atomix.group.LocalMember;
import io.atomix.variables.DistributedValue;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by hanhansongjiang on 17/6/14.
 */
public class Election1 {

    private static String dir="/Users/hanhansongjiang/shop/review/log/";

    private static String host="localhost";

    public static void  main(String args[]) throws ExecutionException, InterruptedException {



        AtomixReplica replica = AtomixReplica.builder(new Address("localhost", 8700))
                .withStorage(new Storage(dir+"d1"))
                .withTransport(new NettyTransport())
                .build();

       replica.bootstrap().join();

       JoinGrop(replica);



        AtomixReplica replica2 = AtomixReplica.builder(new Address("localhost", 8701))
                .withStorage(new Storage(dir+"d2"))
                .withTransport(new NettyTransport())
                .build();

        replica2.join(new Address("localhost", 8700)).join();

        JoinGrop(replica2);


        AtomixReplica replica3 = AtomixReplica.builder(new Address("localhost", 8702))
                .withStorage(new Storage(dir+"d3"))
                .withTransport(new NettyTransport())
                .build();

        replica3.join(new Address("localhost", 8700), new Address("localhost", 8701)).join();

        JoinGrop(replica3);

        List<Address> cluster = Arrays.asList(
                new Address(host, 8700),
                new Address(host, 8701),
                new Address(host, 8702)
        );

        AtomixClient client = AtomixClient.builder()
                .withTransport(new NettyTransport())
                .build();

        client.connect(cluster).join();


//        DistributedValue<Object> value = client.getValue("value").join();
//
//// Set the value and wait for completion of the operation
//        value.set("Hello world!").join();
//
//// Read the value and call the provided callback on response
//        value.get().thenAccept(result -> {
//            System.out.println("The value is " + result);
//        });

    }

    //
    public  static void JoinGrop(AtomixReplica atomixReplica) throws ExecutionException, InterruptedException {
        DistributedGroup group = atomixReplica.getGroup("group").get();
// 加入该group
        LocalMember localMember = group.join().get();
// 获得本节点的唯一标示id
        System.out.println("本节点id:" + localMember.id());
// 选举回调
        group.election().onElection(term -> {
            // term为当前选举任期信息
            if (term.leader().equals(localMember)) {
                System.out.println("选上领导了"+localMember);
            } else {
                System.out.println("没有选上,Leader：" +localMember);
            }
        });
// 离开group
////        localMember.leave();
//        group.members().forEach(v->{
//            System.out.println("group memeber：" +v.id());
//
//        });

    }
}
