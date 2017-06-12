package com.hsj.con.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by hanhansongjiang on 17/6/12.
 */
public class CglibProxyStrategy implements MethodInterceptor,proxyStrategy {

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        System.out.println("begin");

        Object o1 = methodProxy.invokeSuper(o, objects);
        System.out.println("end");

        return o1;


    }

    public Object createProxyObject(Object target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

}
