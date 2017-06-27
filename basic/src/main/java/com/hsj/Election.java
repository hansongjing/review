package com.hsj;/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.atomix.AtomixClient;
import io.atomix.AtomixReplica;
import io.atomix.catalyst.serializer.SerializableTypeResolver;
import io.atomix.catalyst.serializer.SerializerRegistry;
import io.atomix.catalyst.transport.Address;
import io.atomix.catalyst.transport.netty.NettyTransport;
import io.atomix.collections.DistributedMap;
import io.atomix.collections.DistributedMultiMap;
import io.atomix.collections.DistributedQueue;
import io.atomix.collections.DistributedSet;
import io.atomix.concurrent.DistributedLock;
import io.atomix.copycat.client.CopycatClient;
import io.atomix.copycat.server.Commit;
import io.atomix.copycat.server.storage.Storage;
import io.atomix.group.DistributedGroup;
import io.atomix.group.GroupMember;
import io.atomix.group.LocalMember;
import io.atomix.group.messaging.MessageConsumer;
import io.atomix.group.messaging.MessageProducer;
import io.atomix.group.messaging.MessageProducer.Delivery;
import io.atomix.group.messaging.MessageProducer.Execution;
import io.atomix.group.messaging.MessageProducer.Options;
import io.atomix.resource.ResourceFactory;
import io.atomix.resource.ResourceStateMachine;
import io.atomix.resource.ResourceTypeInfo;
import io.atomix.variables.AbstractDistributedValue;
import io.atomix.variables.DistributedLong;
import io.atomix.variables.DistributedValue;
import io.atomix.variables.internal.LongCommands;
import io.atomix.variables.internal.LongCommands.DecrementAndGet;
import io.atomix.variables.internal.LongCommands.IncrementAndGet;
import io.atomix.variables.internal.LongCommands.LongCommand;

/**
 * 集群选举
 *
 * @author gongzuo.zy
 * @version $Id: Study.java, v0.1 2017-04-21 16:42  gongzuo.zy Exp $
 */
public class Election {

    private static String  storge="/Users/hanhansongjiang/shop/review/log";

    // 定义集群所有地址
    static List<Address> clusters = Arrays.asList(Server1.address, Server2.address, Server3.address, Server4.address, Server5.address);

    private static class Server1 {
        static Address address = new Address("localhost", 5001);
        static AtomixReplica atomix = replica(address, storge);

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            atomix.bootstrap(clusters).join();
            joinGroup(atomix);
        }
    }

    private static class Server2 {
        static Address address = new Address("localhost", 5002);
        static AtomixReplica atomix = replica(address, "/Users/hanhansongjiang/shop/review/log");

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            atomix.bootstrap(clusters).join();
            joinGroup(atomix);
        }
    }

    private static class Server3 {
        static Address address = new Address("localhost", 5003);
        static AtomixReplica atomix = replica(address, "/Users/gongzuo.zy/Desktop/t/d3");

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            atomix.bootstrap(clusters).join();
            joinGroup(atomix);
        }
    }

    private static class Server4 {
        static Address address = new Address("localhost", 5004);
        static AtomixReplica atomix = replica(address, "/Users/gongzuo.zy/Desktop/t/d4");

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            atomix.bootstrap(clusters).join();
            joinGroup(atomix);
        }
    }

    private static class Server5 {
        static Address address = new Address("localhost", 5005);
        static AtomixReplica atomix = replica(address, "/Users/gongzuo.zy/Desktop/t/d5");

        public static void main(String[] args) throws ExecutionException, InterruptedException {
            atomix.bootstrap(clusters).join();
            joinGroup(atomix);
        }
    }

    private static void joinGroup(AtomixReplica atomix) throws ExecutionException, InterruptedException {
        DistributedGroup group = atomix.getGroup("group").get();
        LocalMember localMember = group.join().get();

        /*
         * 可以监听整个集群离开和加入的group信息
         */
        group.onJoin(m->System.out.println("加入成功id：" + m.id()));
        group.onLeave(m->System.out.println("离群成功id：" + m.id()));

        /*
         * group可以设定序列化机制，当收到消息可以直接进行序列化使用，需要实现TypeSerializer接口
         *
         */
        group.serializer().register(Election.class, aClass -> null);

        /*
         * 进行组播
         */
        MessageProducer.Options options = new MessageProducer.Options()
                .withDelivery(MessageProducer.Delivery.RANDOM)
                .withExecution(Execution.SYNC);

        MessageProducer<String> producer = group.messaging().producer("producerName", options);
        producer.send("broadcast message");

        /*
         * 通过message机制进行监听，客户端可以直接发消息给
         * 任何一个consumer，亦可以广播消息
         * group.messaging可以采用广播，
         * 针对一个member则使用单播
         */
        MessageConsumer<String> consumer = localMember.messaging().consumer("topic");
        consumer.onMessage(m-> {
            System.out.println("收到消息：" + m);
            m.ack();
        });

        /*
         * 遍历所有group内节点
         */
        for (GroupMember groupMember : group.members()) {
            System.out.println(groupMember.id());
        }

        // 选举回调
        group.election().onElection(term -> {
            // term为当前选举任期信息
            if (term.leader().equals(localMember)) {
                System.out.println("选上领导了");
            } else {
                System.out.println("没有选上,Leader：" + term.leader());
            }
        });
    }

    private static AtomixReplica replica(Address address, String storage) {
        return AtomixReplica.builder(address)
                .withTransport(new NettyTransport())
                .withStorage(new Storage(storage))
                .addResourceType(MyResource.class)
                .build();
    }


    // 创建我们自己的资源
    @ResourceTypeInfo(id=12, factory = MyResourceFactory.class)
    public static class MyResource extends AbstractDistributedValue<DistributedLong, Long> {
        protected MyResource(CopycatClient client, Properties options) {
            super(client, options);
        }

        // 对外提供的方法
        public CompletableFuture<Long> incrementAndGet() {
            return client.submit(new Client.MyResourceResolver.IncrementAndGet());
        }
    }


    public static class MyResourceFactory implements ResourceFactory<MyResource>  {

        @Override
        public SerializableTypeResolver createSerializableTypeResolver() {
            return new Client.MyResourceResolver();
        }

        @Override
        public ResourceStateMachine createStateMachine(Properties properties) {
            return new Client.MyStateMachine(properties);
        }

        @Override
        public MyResource createInstance(CopycatClient copycatClient, Properties properties) {
            return new MyResource(copycatClient, properties);
        }
    }


    private static class Client {

        // 发送广播消息
        static void messaging() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();
            DistributedGroup group = client.getGroup("group").get();
            group.messaging().producer("topic")
                    .send("this is my message").thenAccept(System.out::println);
        }


        // 定义状态机
        static class MyStateMachine extends ResourceStateMachine {

            long value;

            protected MyStateMachine(Properties config) {
                super(config);
            }

            public long incrementAndGet(Commit<IncrementAndGet> commit) {
                try {
                    Long oldValue = value;
                    value = oldValue + 1;
                    return value;
                } finally {
                    commit.close();
                }
            }
        }

        // 注册操作状态
        static class MyResourceResolver implements SerializableTypeResolver {
            public static class IncrementAndGet extends LongCommand<Long> {
            }

            @Override
            public void resolve(SerializerRegistry registry) {
                registry.register(LongCommands.IncrementAndGet.class, -1100);
            }
        }



        static void multimap() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();
            DistributedMultiMap<String, String> map = client.<String, String>getMultiMap("my_multimap").get();
            map.put("k", "v1").join();
            map.put("k", "v2").join();
        }

        static void queue() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();
            DistributedQueue<String> queue = client.<String>getQueue("my_queue").get();
            queue.offer("a");
            queue.peek().join();
            queue.remove().join();
            queue.poll().join();
        }

        static void set() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();
            CompletableFuture<DistributedSet<String>> completableFuture = client.getSet("my_set");
            DistributedSet<String> set = completableFuture.get();

            // 同步调用
            if (!set.contains("key").join()) {
                set.add("key").join();
            } else {
                System.out.println("含有key");
            }

            set.contains("key").thenAccept(contains->{
                if (contains) {
                    System.out.println("含有key");
                } else {
                    set.add("key").thenRun(()-> System.out.println("添加成功"));
                }
            });
        }

        static void map() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();
            CompletableFuture<DistributedMap<String, String>> completableFuture = client.getMap("my_map");

            DistributedMap<String, String> map = completableFuture.join();

            // 同步调用
            if (map.containsKey("key").join()) {
                String value = map.get("key").join();
                System.out.println(value);
            } else {
                //do others
            }

            // 同步调用
            map.putIfAbsent("key", "value").join();

            //异步调用
            map.containsKey("key").thenAccept(containsKey -> {
                if (containsKey) {
                    map.get("key").thenAccept(System.out::println);
                } else {
                    //do others
                }
            });

            map.putIfAbsent("key", "value").thenRun(()->{
                System.out.println("success");
            });
        }

        static void lock() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();
            CompletableFuture<DistributedLock> completableFuture =  client.getLock("my_lock");

            // 异步api
            completableFuture.thenAccept(lock -> {
                lock.lock().thenRun(()->System.out.println("得到了锁"));
            });

            // 同步api
            DistributedLock lock = completableFuture.get();
            lock.lock().join();
        }

        static void variable() throws ExecutionException, InterruptedException {
            AtomixClient client = connect();

            DistributedValue<String> value = client.<String>getValue("test-value").get();

            // 同步获得数据
            System.out.println(value.get().join());

            // 不带有过期时间的设置
            value.set("lala");

            // 带有时间的设置
            value.set("lala", Duration.ofDays(10));

            // 异步获得数据
            value.get().thenAccept(System.out::println);

            // 获得long变量
            DistributedLong distributedLong = client.getLong("test-long").get();

            // long的基本操作
            System.out.println(distributedLong.getAndDecrement().join());
            System.out.println(distributedLong.getAndAdd(10).join());
            System.out.println(distributedLong.getAndDecrement().join());
        }

        static AtomixClient connect() throws ExecutionException, InterruptedException {
            // 创建客户端
            AtomixClient client = AtomixClient.builder()
                    .withTransport(new NettyTransport())
                    .build();

            // 异步链接clusters
            client.connect(clusters).thenRun(() -> {
                System.out.println("Client connected!");
            }).get();

            // 同步链接
            //client.connect(clusters).join();

            return client;
        }
    }

    public static void main(String args[]){


    }
}