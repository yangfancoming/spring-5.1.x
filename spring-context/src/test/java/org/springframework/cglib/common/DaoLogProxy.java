package org.springframework.cglib.common;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法执行【日志】代理类
 * Created by 64274 on 2019/4/9.
 * @ Description: 拦截父类所有方法的调用
 * @ author  山羊来了
 * @ date 2019/4/9---18:11
 */
public class DaoLogProxy implements MethodInterceptor {

    /**
     Object 表示要进行增强的对象
     Method 表示拦截的原生方法
     Object[] 当前被代理方法的参数列表，基本数据类型需要传入其包装类型，如int-->Integer、long-Long、double-->Double
     MethodProxy 表示对原生方法的代理，invokeSuper 方法表示对被代理对象方法的调用
    */
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("cglib动态代理 前置增强  方法名" + method.getName());
        proxy.invokeSuper(object, args); // 被代理类中的所有方法
		System.out.println("cglib动态代理 后置增强  方法名" + method.getName());
        return object;
    }
}