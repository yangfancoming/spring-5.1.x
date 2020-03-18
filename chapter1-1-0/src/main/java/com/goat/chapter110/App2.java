package com.goat.chapter110;


import com.goat.chapter110.bean.Cat;
import com.goat.chapter110.bean.Dog;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * 测试二： 自定义Bean的初始化和销毁方法
 * 通过让 Bean 实现 InitializingBean（定义初始化逻辑），DisposableBean（定义销毁逻辑）
 */
public class App2 {

	@Test
	public void test4(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Cat.class);
		ctx.close(); // 单实例Bean 关闭容器后  会调用我们自定义的销毁方法
	}

	/**  测试三： 自定义Bean的初始化和销毁方法
	 *  通过使用JSR250 规范中的
	 * 		@PostConstruct： 在bean创建完成并且属性赋值完成；来执行初始化方法
	 * 		@PreDestroy： 在容器销毁bean之前通知我们进行清理工作
	 */
	@Test
	public void test5(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Dog.class);
		ctx.close(); // 单实例Bean 关闭容器后  会调用我们自定义的销毁方法
	}

	/**  测试四： 自定义Bean的初始化和销毁方法
	 *  通过使用 BeanPostProcessor 后置处理器
	*/
	@Test
	public void test6(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(MyConfig.class);
		ctx.close();
	}
}
