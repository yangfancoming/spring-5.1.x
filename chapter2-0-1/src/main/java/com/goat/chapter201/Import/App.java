package com.goat.chapter201.Import;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;


/**
 * 给容器中注册组件的四大方式
 * 1）、包扫描 + 组件标注注解（@Controller/@Service/@Repository/@Component）[自己写的类]
 * 2）、@Bean[导入的第三方包里面的组件]
 * 3）、@Import[快速给容器中导入一个组件]
 * 		1）、@Import(要导入到容器中的组件)；容器中就会自动注册这个组件，id默认是全类名
 * 		2）、ImportSelector:返回需要导入的组件的全类名数组；
 * 		3）、ImportBeanDefinitionRegistrar:手动注册bean到容器中
 * 4）、使用Spring提供的 FactoryBean（工厂Bean）;
 * 		1）、默认获取到的是工厂bean调用getObject创建的对象
 * 		2）、要获取工厂Bean本身，我们需要给id前面加一个&   例如：&colorFactoryBean
 */
public class App {

	@Test
	public void ImportConfig(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ImportConfig.class);
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 源码位置 ConfigurationClassParser#processImports()
	 * @Import 导入简单类，id 默认是组件的全类名   @Import({Blue.class, Red.class})
	*/
	@Test
	public void ImportConfig1(){
		new AnnotationConfigApplicationContext(ImportConfig1.class);
	}

	/**
	 * 源码位置 ConfigurationClassParser#processImports()
	 * @Import 导入配置类，相当于xml中的 <import> 标签
	 */
	@Test
	public void test(){
		new AnnotationConfigApplicationContext(ImportConfig2.class);
	}

	/**
	 * ImportSelector 导入组件，id 默认是组件的全类名
	 */
	@Test
	public void ImportSelectorConfig(){
		new AnnotationConfigApplicationContext(ImportSelectorConfig.class);
	}

	/**
	 * ImportBeanDefinitionRegistrar 接口 导入组件
	*/
	@Test
	public void ImportBeanDefinitionRegistrarConfig1(){
		new AnnotationConfigApplicationContext(ImportBeanDefinitionRegistrarConfig1.class);
	}

	@Test
	public void ImportBeanDefinitionRegistrarConfig2(){
		new AnnotationConfigApplicationContext(ImportBeanDefinitionRegistrarConfig2.class);
	}
	
	/**
	 * DeferredImportSelector 导入组件
	 * @see ConfigurationClassParser#processImports(org.springframework.context.annotation.ConfigurationClass, org.springframework.context.annotation.ConfigurationClassParser.SourceClass, java.util.Collection, boolean)
	 * @see ConfigurationClassParser.DeferredImportSelectorHandler#process()
	 */
	@Test
	public void deferredImportSelectorTest(){
		new AnnotationConfigApplicationContext(DeferredImportSelectorConfig.class);
	}
}
