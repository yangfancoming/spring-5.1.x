package com.goat.chapter185.item07.sample2;


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
 * 这样一来，一下子就解决问题了。在上面分析的时候我们看到AbstractAutowireCapableBeanFactory默认采用的创建Bean策略器为：
 * private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();
 * 因此默认就是支持方法注入的，所以当我们方法标注了@Lookup注解，就能达到我们上诉的效果了~
 *  了解了此处，对后续AOP原理讲解的时候，也有非常大的帮助~~~ 看来提前讲解还是不亏的，哈哈
 */
public class App {

	@Test
	public void test(){
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext(RootConfig.class);
		A a = applicationContext.getBean(A.class);
		// 不仅仅每次getB都不一样了，我们还发现a已经变成了一个CGLIB的代理对象
		System.out.println(a); //com.fsx.bean.A$$EnhancerBySpringCGLIB$$558725dc@6a6cb05c
		System.out.println(a.getB() == a.getB()); //false
	}
}
