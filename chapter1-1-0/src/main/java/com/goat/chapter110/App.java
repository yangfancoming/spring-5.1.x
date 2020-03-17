package com.goat.chapter110;


import com.goat.chapter110.bean.*;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App   {

	/**  测试  构造函数（容器对象创建）
	 * 		单实例的Bean：在容器启动的时候就创建对象
	 * */
	@Test
	public void test1(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanLifeCycle.class);
		System.out.println("容器创建完成");
		ctx.close();
	}

	/**  测试  构造函数（容器对象创建）
	 * 		多实例的Bean：只有在每次获取的时候才创建对象 (但是容器不会管理 多实例Bean的销毁)
	 * */
	@Test
	public void test2(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanLifeCycle2.class);
		System.out.println(ctx);
		System.out.println("多实例的Bean 只有在每次获取的时候才创建对象");
		ctx.getBean("car");
		ctx.close(); // 多实例Bean 关闭容器后 不会调用我们自定义的销毁方法
	}

	/**  测试一： 自定义Bean的初始化和销毁方法
	 * 通过 @Bean 指定 init-method 和 destroy-method
	 * doit 为啥  BeanLifeCycle3.class 类中 去掉@Configuration注解也是可以的呢？？？
	 * */
	@Test
	public void test3(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanLifeCycle3.class);
		System.out.println("容器创建完成");
		ctx.close(); // 单实例Bean 关闭容器后  会调用我们自定义的销毁方法
	}


	/**  测试二： 自定义Bean的初始化和销毁方法
	 *  通过让 Bean 实现 InitializingBean（定义初始化逻辑），DisposableBean（定义销毁逻辑）
	 * */
	@Test
	public void test4(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Cat.class);
		ctx.close(); // 单实例Bean 关闭容器后  会调用我们自定义的销毁方法
	}

	/**  测试三： 自定义Bean的初始化和销毁方法
	 *  通过使用JSR250 规范中的
	 * 		@PostConstruct： 在bean创建完成并且属性赋值完成；来执行初始化方法
	 * 		@PreDestroy： 在容器销毁bean之前通知我们进行清理工作
	 * */
	@Test
	public void test5(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Dog.class);
		ctx.close(); // 单实例Bean 关闭容器后  会调用我们自定义的销毁方法
	}

	/**  测试四： 自定义Bean的初始化和销毁方法
	 *  通过使用 BeanPostProcessor 后置处理器
	 * */
	@Test
	public void test6(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MyConfig.class);
		System.out.println(ctx);
		ctx.close();
	}
}
