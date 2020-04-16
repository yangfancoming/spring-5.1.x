package org.springframework.cglib.item01;


import org.junit.Test;
import org.springframework.cglib.proxy.Enhancer;

/**
 * Created by 64274 on 2019/4/9.
 *
 * @ Description: 使用Cglib代码对类做代理
 * @ author  山羊来了
 * @ date 2019/4/9---18:13
 *
 * cglib是针对类来实现代理的，原理是对指定的业务类生成一个子类，并覆盖其中业务方法实现代理。因为采用的是继承，所以不能对final修饰的类进行代理
 */
public class App {

    @Test
    public void testCglib() {

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Dao.class); // setSuperclass 表示设置要代理的类
        enhancer.setCallback(new DaoProxy());  // setCallback 表示设置回调即 MethodInterceptor 的实现类

        Dao dao = (Dao)enhancer.create(); // 使用create()方法 动态创建一个代理对象
        dao.update();
        dao.select();
    }
}
