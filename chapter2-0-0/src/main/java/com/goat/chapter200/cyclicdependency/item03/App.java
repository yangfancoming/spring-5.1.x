package com.goat.chapter200.cyclicdependency.item03;

import org.junit.Test;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: 三级缓存方式 解决循环依赖
 * @ author  山羊来了
 * @ date 2020/4/3---15:15
 */
public class App {

	/**
	 * 项目启动成功，能够正常work
	 * 入口
	 * @see AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 * 第一次 获取bean a 由于 isSingletonCurrentlyInCreation 为 false 直接返回 null
	 * @see DefaultSingletonBeanRegistry#getSingleton(java.lang.String, boolean)
	 * 第二次 获取bean a 由于 ObjectFactory 接口 回调 createBean
	 * @see DefaultSingletonBeanRegistry#getSingleton(java.lang.String, org.springframework.beans.factory.ObjectFactory)
	 * 设置 isSingletonCurrentlyInCreation 标记为true  标识bean a 正在创建中。。。
	 * @see DefaultSingletonBeanRegistry#beforeSingletonCreation(java.lang.String)
	 * 通过无参构造函数创建 bean a （实例化）  纯净态
	 * @see AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	 * @see AbstractAutowireCapableBeanFactory#createBeanInstance(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	 * 由于 isSingletonCurrentlyInCreation 为 true  将a添加到三级缓存
	 * @see DefaultSingletonBeanRegistry#addSingletonFactory(java.lang.String, org.springframework.beans.factory.ObjectFactory)
	 *  a bean已经实例化 并且已经添加到三级缓存，接下来要开始填充属性
	 * @see AbstractAutowireCapableBeanFactory#populateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.BeanWrapper)
	 *  发现 a 依赖 b  又开始重新走流程去创建b
	 * @see AutowiredAnnotationBeanPostProcessor#postProcessProperties(org.springframework.beans.PropertyValues, java.lang.Object, java.lang.String)
	 * @see DependencyDescriptor#resolveCandidate(java.lang.String, java.lang.Class, org.springframework.beans.factory.BeanFactory)
	 *  发现 b 依赖 a  又开始重新走流程去创建a    由于a已经在三级缓存中，通过三级缓存的工厂回调获取到提前暴露的a，并将a添加到二级缓存
	 *  有了a的早起引用就可以实例化b，实例化b之后，就可以继续去实例化a
	*/
	@Test
	public void test(){
		new AnnotationConfigApplicationContext(Config.class);
	}
}
