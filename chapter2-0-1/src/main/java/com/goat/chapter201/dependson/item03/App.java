package com.goat.chapter201.dependson.item03;

import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2021/7/5.
 * @ Description: 测试 @DependsOn 注解的循环依赖问题
 * @ author  山羊来了
 * @ date 2021/7/5---17:36
 *
 * 方法isDependent主要用于检测bean是否存在循环依赖情况。如：A依赖B，B依赖C，C依赖A（A->B->C->A）
 *  方便解释将方法isDependent称为XM
 *  方便解释将方法registerDependentBean称为YM
 *
 * A依赖B
 * 第一步，调用XM，参数为beanName=A，dependentBeanName=B，alreadySeen=null
 * 第二步，检测dependentBeanMap能否获取到A，获取不到，dependentBeans为空，不存在依赖关系。
 * 第三步，调用YM，dependentBeanMap添加key=B，value=A（即为ependentBeans）
 *
 * B依赖C
 * 第一步，调用XM，参数为beanName=B，dependentBeanName=C，alreadySeen=null
 * 第二步，检测dependentBeanMap能否获取到B，可以获取到，dependentBeans不为空，已存在A
 * 第三步，dependentBeans没有包含C。
 * 第四步，迭代dependentBeans，目前就存在一个A
 * 第五步，alreadySeen 添加B
 * 第六步，调用XM，参数为beanName=A，dependentBeanName=C，alreadySeen=有B
 * 第七步，检测dependentBeanMap能否获取到A，获取不到，dependentBeans为空，不存在依赖关系。
 * 第八步，调用YM，dependentBeanMap添加key=C，value=B（即为ependentBeans）
 *
 * C依赖A
 * 第一步，beanName=C，dependentBeanName=A，alreadySeen=null
 * 第二步，检测dependentBeanMap能否获取到C，可以获取到，dependentBeans不为空，已存在B
 * 第三步，dependentBeans没有包含A。
 * 第四步，alreadySeen添加C
 * 第五步，调用XM，参数为beanName=B，dependentBeanName=A，alreadySeen=有C
 * 第六步，检测dependentBeanMap能否获取到B，可以获取到，dependentBeans不为空，已存在A
 * 第七步，dependentBeans包含A，已检测到存在循环依赖。
 */
public class App {

	/**
	 * 测试 两个map的存储结构：
	 * @see DefaultSingletonBeanRegistry#dependentBeanMap
	 * @see DefaultSingletonBeanRegistry#dependenciesForBeanMap
	*/
	@Test
	public void test3(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config3.class);
		Arrays.stream(ac.getBeanDefinitionNames()).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 两个Bean 触发 dependsOn 循环依赖异常  示例
	 * @see AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 * @see DefaultSingletonBeanRegistry#isDependent(java.lang.String, java.lang.String)
	 * @see DefaultSingletonBeanRegistry#registerDependentBean(java.lang.String, java.lang.String)
	 * @see AbstractBeanFactory#getBean(java.lang.String)
	*/
	@Test(expected = BeanCreationException.class)
	public void test1(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config1.class);
		Arrays.stream(ac.getBeanDefinitionNames()).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 三个Bean 触发 dependsOn 循环依赖异常 示例
	*/
	@Test(expected = BeanCreationException.class)
	public void test2(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config2.class);
		Arrays.stream(ac.getBeanDefinitionNames()).forEach(x->System.out.println("***---***	 " + x));
	}
}
