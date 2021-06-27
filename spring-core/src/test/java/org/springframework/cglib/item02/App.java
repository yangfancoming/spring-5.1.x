package org.springframework.cglib.item02;


import org.junit.Test;
import org.springframework.cglib.common.DaoLogProxy;
import org.springframework.cglib.common.MyDao;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;

/**
 * 使用cglib代理，根据被代理类中的不同方法，执行不同的代理策略
 * Created by 64274 on 2019/4/9.
 * @ Description: 再扩展一点点，比方说在AOP中我们经常碰到的一种复杂场景是：我们想对类A的B方法使用一种拦截策略、类A的C方法使用另外一种拦截策略。
 * @ author  山羊来了
 * @ date 2019/4/9---18:13
 */
public class App {

    /**
	 * 意思是CallbackFilter的accept方法返回的数值表示的是顺序，顺序和setCallbacks里面Proxy的顺序是一致的。
	 * 再解释清楚一点，Callback数组中有三个callback，那么：
	 *  方法名为"select"的方法返回的顺序为0，即使用Callback数组中的0位callback，即 DaoLogProxy
	 *  方法名为非"select"的方法返回的顺序为1，即使用Callback数组中的1位callback，即 DaoTimeProxy
	 *  update()方法不是方法名为"select"的方法，因此返回1，返回1使用DaoTimeProxy，即打印时间；
	 *  select()方法是方法名为"select"的方法，因此返回0，返回0使用DaoLogProxy，即打印日志。
	 *  这里要额外提一下，Callback数组中我特意定义了一个NoOp.INSTANCE，这表示一个空Callback，即如果不想对某个方法进行拦截，可以在DaoFilter中返回2，具体效果可以自己尝试一下。
    */
    @Test
    public void testCglib() {
        DaoLogProxy daoLogProxy = new DaoLogProxy(); // 创建 日志代理对象
        DaoTimeProxy daoTimeProxy = new DaoTimeProxy(); // 创建 时间代理对象
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MyDao.class);
        // 设置回调过滤器，其返回值定对应  setCallbacks 数组中的下标。
		enhancer.setCallbackFilter(new DaoFilter());
		// 根据 setCallbackFilter 实现类的返回值，来决定执行哪个代理方法。
        enhancer.setCallbacks(new Callback[]{daoLogProxy, daoTimeProxy, NoOp.INSTANCE});
        /**
		 * 如果想要在构造函数中调用update()方法时，不拦截的话，
		 * Enhancer中有一个setInterceptDuringConstruction(boolean interceptDuringConstruction)方法设置为false即可，默认为true，
		 * 即构造函数中调用方法也是会拦截的
        */
        enhancer.setInterceptDuringConstruction(false);
		// 使用字节码技术动态创建子类的实例（它是业务类的子类，可以用业务类引用指向它）。
        MyDao dao = (MyDao)enhancer.create();
		// 最后通过动态代理类对象进行方法调用。
        dao.update(); // 对类A的B方法使用一种拦截策略
        dao.select(); // 类A的C方法使用另外一种拦截策略
    }
}
