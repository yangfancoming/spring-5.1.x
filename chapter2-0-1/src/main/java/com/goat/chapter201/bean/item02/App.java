package com.goat.chapter201.bean.item02;

import com.goat.chapter201.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.PostProcessorRegistrationDelegate;

/**
 * Created by Administrator on 2021/7/1.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/1---17:04
 *
 * 记住这儿是在调用Bean工厂的后置处理器之前合并BeanDefinition的，那么可能有读者会问，
 * 既然后面可以调用Bean工厂的后置处理器来对BeanDefinition的信息进行修改，那么这儿为什么要进行合并？
 * 直接等到创建Bean的实例之前再和平就好了，因为这儿合并一次后，后面如果BeanDefinition的信息修改了，
 * 那么后面又要合并一次，不是这次的合并是没有意义的吗？
 * 其实不然，因为这儿的合并并不是作用普通的BeanDefinition,因为spring有些Bean是开天辟地的，
 * 用来扫描的什么的？这些BeanDefinition不会经过对应的Bean工厂的后置处理器修改，
 * 因为这些类在是在调用Bean工厂的后置器之前就创建了，所以防止一部分的扩展中用到了parentName，
 * 所以在这儿就进行了一次合并。
 * 并不冲突，正常的情况下，我们这儿的合并不是在这儿调用，而是在我们扫描出来BeanDefinition后再进行合并的。
 */
public class App {

	/**
	 * bean定义合并时机一：
	 * @see PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(org.springframework.beans.factory.config.ConfigurableListableBeanFactory, java.util.List)
	 * @see ListableBeanFactory#getBeanNamesForType(java.lang.Class, boolean, boolean)
	 * @see DefaultListableBeanFactory#doGetBeanNamesForType(org.springframework.core.ResolvableType, boolean, boolean)
	 * @see AbstractBeanFactory#getMergedLocalBeanDefinition(java.lang.String)
	 *
	 * bean定义合并时机二：
	 * @see AbstractApplicationContext#finishBeanFactoryInitialization(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 * @see ConfigurableListableBeanFactory#preInstantiateSingletons()
	 * @see AbstractBeanFactory#getMergedLocalBeanDefinition(java.lang.String)
	 *
	 * bean定义合并时机三：
	 * @see AbstractBeanFactory#isTypeMatch(java.lang.String, org.springframework.core.ResolvableType)
	 * @see AbstractBeanFactory#getMergedLocalBeanDefinition(java.lang.String)
	*/
	@Test
	public void test(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		// 配置并注册 父bean定义
		RootBeanDefinition rootBean = new RootBeanDefinition(RootBean.class);
		rootBean.getPropertyValues().add("name","张三");
		rootBean.getPropertyValues().add("age","18");
		context.registerBeanDefinition("root", rootBean);
		// 配置并注册 子bean定义
		GenericBeanDefinition childBean = new GenericBeanDefinition();
		childBean.setBeanClass(ChildBean.class);
		childBean.getPropertyValues().add("name", "李四");
		// 设置 子bean与父bean关联
		childBean.setParentName("root");
		context.registerBeanDefinition("child", childBean);
		// 触发刷新上下文
		context.refresh();
		// 取出bean定义 查看结果
		ChildBean bean = context.getBean(ChildBean.class);

		// 可以看到  我们并没有为子bean的age属性赋值，但是子bean中的age属性为18
		// 是新建一个父bean，然后子bean去覆盖父bean得到的。
		Assert.assertSame("李四",bean.getName());
		Assert.assertSame("18",bean.getAge());
	}


	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("parent.xml");
		User user = (User)ac.getBean("user");
		System.out.println(user);
	}
}
