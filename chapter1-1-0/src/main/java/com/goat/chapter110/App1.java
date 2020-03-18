package com.goat.chapter110;


import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**  测试一： 自定义Bean的初始化和销毁方法
 * 通过 @Bean 指定 init-method 和 destroy-method
 * doit 为啥  BeanLifeCycle2.class 类中 去掉@Configuration注解也是可以的呢？？？
 */
public class App1 {

	/**  测试  构造函数（容器对象创建）
	 * 		单实例的Bean：在容器启动的时候就创建对象
	 */
	@Test
	public void test1(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanLifeCycle.class);
		System.out.println("容器创建完成");
		ctx.close();
	}


	@Test
	public void test3(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanLifeCycle2.class);
		System.out.println("容器创建完成");
		ctx.close(); // 单实例Bean关闭容器后  才会调用我们自定义的销毁方法
	}

	/**  测试  构造函数（容器对象创建）
	 * 		多实例的Bean：只有在每次获取的时候才创建对象 (但是容器不会管理 多实例Bean的销毁)
	 */
	@Test
	public void test2(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeanLifeCycle3.class);
		System.out.println("多实例的Bean 只有在每次获取的时候才创建对象");
		ctx.getBean("car");
		ctx.close(); // 多实例Bean 关闭容器后 不会调用我们自定义的销毁方法
	}

}
