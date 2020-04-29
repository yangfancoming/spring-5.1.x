package org.springframework.core.io;

import org.junit.Assert;
import org.junit.Test;


/**
 * Created by Administrator on 2020/4/28.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/28---17:24
 */
public class DefaultResourceLoaderTest {

	// 步骤1，先用扩展协议解析器解析资源地址并返回。举个例子，咱们可以自定义资源解析器来完成带前缀“classpath:”的解析：
	@Test
	public void test1(){
		DefaultResourceLoader bf = new DefaultResourceLoader();
		bf.getProtocolResolvers().add(new MyProtocolResolver());
		try {
			Resource resource = bf.getResource("classpath:log4j2-test.xml");
			Assert.assertEquals("file:/E:/Code/Spring/GitHub2/spring-framework-5.1.x/spring-core/out/test/resources/log4j2-test.xml",resource.getURI().toURL().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 步骤2，假设location以斜杠开头，则调用该类中 getResourceByPath(String path)方法
	 * ClassPathContextResource继承自ClassPathResource并实现了ContextResource，
	 * 因此该类可以表示资源的地址为web容器中的项目的类文件存放路径，
	 * 假设咱们现有一个web项目叫chapter3,则ClassPathContextResource可以表示/WEB-INF/classes/下的资源文件。
	 * 咱们可以在web.xml中做如下配置：
	 * <context-param>
	 *     <param-name>contextConfigLocation</param-name>
	 * 	<param-value>/WEB-INF/classes/smart-context.xml</param-value>
	 * </context-param>
	*/
	@Test
	public void test2(){
		// XmlBeanDefinitionReader - Loading XML bean definitions from ServletContext resource [/WEB-INF/classes/smart-context.xml]
	}

	/**
	 * 步骤三，假如资源表达式以classpath开头，则截取除前缀calsspath:的路径，并做为ClassPathResource的构造参数，
	 * 生成ClassPathResource实例后返回。咱们可以在web.xml中做如下配置：
	 * <context-param>
	 *     <param-name>contextConfigLocation</param-name>
	 * 	<param-value>classpath:smart-context.xml</param-value>
	 * </context-param>
	 */
	@Test
	public void test3(){
		//XmlBeanDefinitionReader - Loading XML bean definitions from class path resource[smart-context.xml]
	}

	/**
	 * 步骤四，如果以上的三个步骤都加载失败，则尝试使用url的方式来加载，因此咱们也可以在web.xml做如下配置：
	 *  <context-param>
	 *  <param-name>contextConfigLocation</param-name>
	 *  <param-value>file:/D:/ALANWANG-AIA/Horse-workspace/chapter3/target/classes/smart-context.xml</param-value>
	 *  </context-param>
	 */
	@Test
	public void test4(){
		//  Loading XML bean definitions from URL [file:/D:/ALANWANG-AIA/Horse-workspace/chapter3/target/classes/smart-context.xml]
	}

	/**
	 * 步骤五，如果url的方式也加载失败了，则尝试再次使用classpath 的方式加载，这时，咱们的web.xml 可以这样写：
	 * <context-param>
	 *     <param-name>contextConfigLocation</param-name>
	 * 	<param-value>WEB-INF/classes/smart-context.xml</param-value>
	 * </context-param>
	 */
	@Test
	public void test5(){
		//  XmlBeanDefinitionReader - Loading XML bean definitions from ServletContext resource [/WEB-INF/classes/smart-context.xml]
	}

	/**
	 * 总结：
	 * 1.spring对框架的使用者非常友好，很多地方都留有接口供使用者扩展，而且是优先调用。
	 *
	 * 2.DefaultResourceLoader虽然提供了众多的资源表达式，但是带前缀classpath:在书写上比其他几种更为简便，在执行顺序上位于第二，仅次于扩展接口，那么在效率上也优于其他书写方式（虽然可以忽略不计...）
	 *
	 * 3.带classpath前缀的写法更易于移植，相对于一般的java程序，classpath指向的是类文件存放的位置，而对于web应用来说，classpath指向的是WEB-INF/classes,因此classpath: 的写法还有屏蔽不同应用类型的作用，实际开发中也以classpath:前缀的写法居多，所以在日后的开发中，我选classpath:前缀。
	 *
	 * 4.DefaultResourceLoader的getResource(String location)可以看作为是一个模板方法，getResourceByPath(String path) 是钩子函数，只不过DefaultResourceLoader自身也为其提供了实现，
	 *
	 * protected Resource getResourceByPath(String path) {
	 * 		return new ClassPathContextResource(path, getClassLoader());
	 *        }
	 * 因此DefaultResourceLoader可以单独使用，也可以搭配子类。子类通过重写getResourceByPath(String path)来返回和自身相关的资源类型，从而达到利用不同方式加载资源的目的。
	*/
}
