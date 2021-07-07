package com.goat.chapter201.bean.primary;
import com.goat.chapter201.model.A;
import org.junit.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2021/7/1.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/1---19:23
 */
public class App {

	/**
	 * 测试  容器中有2个名称不同但是bd相同的bean，在按类型获取时会触发NoUniqueBeanDefinitionException异常
	 * @see DefaultListableBeanFactory#getBean(java.lang.Class)
	 * @see DefaultListableBeanFactory#determinePrimaryCandidate(java.util.Map, java.lang.Class)
	 * @see DefaultListableBeanFactory#determineHighestPriorityCandidate(java.util.Map, java.lang.Class)
	 * 经过 determinePrimaryCandidate 和 determineHighestPriorityCandidate  都未能从多个候选者中，筛选出优先bean的话，则抛出异常。
	 * No qualifying bean of type 'com.goat.chapter201.bean.primary.A' available: 【expected single matching bean but found 2: a1,a2】
	 */
	@Test(expected = NoUniqueBeanDefinitionException.class)
	public void test1(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config1.class);
		context.getBean(A.class);
	}

	/**
	 * 两个bd中 有一个标有 @Primary 注解，则可以正常注入
	*/
	@Test
	public void test2(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config2.class);
		context.getBean(A.class);
	}

	/**
	 * 两个bd中 都标有 @Primary 注解，则 抛出异常，因为候选bean中最多只能有一个bd标注@Primary注解
	 *  No qualifying bean of type 'com.goat.chapter201.model.A' available: 【more than one 'primary' bean found among candidates: [a1, a2]】
	 */
	@Test(expected = NoUniqueBeanDefinitionException.class)
	public void test3(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config3.class);
		context.getBean(A.class);
	}
}
