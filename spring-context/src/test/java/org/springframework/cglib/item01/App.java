package org.springframework.cglib.item01;


import org.junit.Test;
import org.springframework.cglib.common.DaoLogProxy;
import org.springframework.cglib.common.MyDao;
import org.springframework.cglib.proxy.Enhancer;

/**
 * Created by 64274 on 2019/4/9.
 * @ Description: 使用Cglib代码对类做代理
 * @ author  山羊来了
 * @ date 2019/4/9---18:13
 * cglib是针对类来实现代理的，原理是对指定的业务类生成一个子类，并覆盖其中业务方法实现代理。因为采用的是继承，所以不能对final修饰的类进行代理
 */
public class App {

	Enhancer enhancer = new Enhancer();

    @Test
    public void testCglib1() {
		// 设置要被代理的类
        enhancer.setSuperclass(MyDao.class);
		// 设置回调（MethodInterceptor接口实现类）
        enhancer.setCallback(new DaoLogProxy());
		// 使用create()方法 动态创建一个代理对象
        MyDao dao = (MyDao)enhancer.create();
        dao.update();
        dao.select();
    }

}
