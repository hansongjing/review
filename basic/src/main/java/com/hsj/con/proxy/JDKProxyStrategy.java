package com.hsj.con.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by hanhansongjiang on 17/6/12.
 */
public class JDKProxyStrategy implements InvocationHandler,proxyStrategy {

    private Object target;//代理目标对象

    public JDKProxyStrategy(Object object) {
        this.target = object;
    }

    public Object createProxyObject(Object target) {
        this.target = target;

        return Proxy.newProxyInstance(this.target.getClass().getClassLoader(), target.getClass().getInterfaces(), this);


    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Count count = (Count) this.target;

        System.out.println("begin count" + count.getCount());
        Object result = null;

            result = method.invoke(target, args);

        System.out.println("end count" + result);

        return result;

    }

    public static void main(String args[]) {
        Count count = new Count(10);
        JDKProxyStrategy jdkProxy = new JDKProxyStrategy(count);

        CountInterface countInterface = (CountInterface) jdkProxy.createProxyObject(count);


        countInterface.count();


    }
}
