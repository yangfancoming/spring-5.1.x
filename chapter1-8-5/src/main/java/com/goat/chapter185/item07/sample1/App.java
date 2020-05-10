package com.goat.chapter185.item07.sample1;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/5/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/10---20:40
 *
 * 关于方法注入（MethodInjection）：
 * 当我们在一个bean中依赖其他的bean时，我们可以注入其他依赖的bean通过set()或者构造器方法。 这样调用get方法的时候返回在bean中注入的实例。但是如果我们希望在每次调用get方法的时候返回新的实例，怎么办呢？
 *
 * 比如单例的A，希望每次使用B的时候都是一个新的对象~
 *  有的伙伴可能就会这么写了：
 *
 *  现在，这个没达到我们的需求嘛。因为每次A类里面使用B的时候，还是同一个实例~
 *  原因：因为A是单例，它只会被实例化一次，因此对应的属性也会被注入一次。所以即使你get()调用了多次，返回的还是第一次赋值的那个属性值
 */
public class App {

	@Test
	public void test(){
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RootConfig.class);
		A a = applicationContext.getBean(A.class);
		// 我们发现，虽然我们给B设置成`prototype`，但是从a里面get出来的每次都还是同一个对象
		System.out.println(a.getB() == a.getB()); //true
		//证明B不是单例了：
		B b1 = applicationContext.getBean(B.class);
		B b2 = applicationContext.getBean(B.class);
		//证明B并不是单例的
		System.out.println(b1 == b2); //false
	}
}
