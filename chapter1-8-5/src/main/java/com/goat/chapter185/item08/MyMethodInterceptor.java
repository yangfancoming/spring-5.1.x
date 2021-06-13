package com.goat.chapter185.item08;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2021/6/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/13---15:20
 */
public class MyMethodInterceptor implements MethodInterceptor {

	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		// 如果是Object中的方法 则直接放行
		if (Object.class.equals(method.getDeclaringClass())) {
			return o;
		}
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ 目标方法前:" + method+"\n");
		Object object = methodProxy.invokeSuper(o, objects);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ 目标方法后:" + method+"\n");
		return object;
	}
}
