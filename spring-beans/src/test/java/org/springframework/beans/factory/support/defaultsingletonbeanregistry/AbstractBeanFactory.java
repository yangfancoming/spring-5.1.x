package org.springframework.beans.factory.support.defaultsingletonbeanregistry;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: 可以看到 AbstractBeanFactory 实现了 ConfigurableBeanFactory 接口 但是必须重写的test1()方法却放在了父类DefaultSingletonBeanRegistry中！
 * @ author  山羊来了
 * @ date 2020/4/3---13:16
 */
public class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements ConfigurableBeanFactory {


}
