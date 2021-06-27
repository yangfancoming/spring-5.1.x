package org.springframework.cglib.item03;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2021/6/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---10:41
 */
public class App {

	@Test
	public void test() throws IllegalAccessException {

		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		TestBean bean = ac.getBean(TestBean.class);
		System.out.println(bean);

		Config config = ac.getBean(Config.class);
		System.out.println(config);

		// 测试cglib 动态生成的 $$beanFactory 属性
		Field field = ReflectionUtils.findField(config.getClass(), "$$beanFactory");
		BeanFactory o = (BeanFactory) field.get(config);

		System.out.println(o == ac.getDefaultListableBeanFactory());
	}

	/**
	 * cglib 给Config主配置类生成的代理类模型为：
	 * public class Config$$EnhancerBySpringCGLIB extends Config implement EnhancedConfiguration {
	 * 		public BeanFactory $$beanFactory;
	 * 		public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
	 * 		    this.$$beanFactory = beanFactory
	 * 		}
	 * }
	*/
}
