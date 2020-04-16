package org.springframework.cglib.item04;

import org.junit.Test;

/**
 * Created by 64274 on 2019/11/4.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/4---14:45
 */
public class App {

    @Test
    public void test()  {
        Engineer engineerProxy = (Engineer) CglibProxy.getProxy(new Engineer());
        engineerProxy.eat(); // 可以被代理
        engineerProxy.work();//  final 方法不会被生成的子类覆盖
    }
}
