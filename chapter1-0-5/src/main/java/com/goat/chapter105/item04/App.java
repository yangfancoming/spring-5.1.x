package com.goat.chapter105.item04;

import com.goat.chapter105.BaseTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


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
 * 		2）、要获取工厂Bean本身，我们需要给id前面加一个&
 * 			&colorFactoryBean
 *
 * 源码位置：
 * doProcessConfigurationClass:252, ConfigurationClassParser (org.springframework.context.annotation)
 * processConfigurationClass:197, ConfigurationClassParser (org.springframework.context.annotation)
 * parse:158, ConfigurationClassParser (org.springframework.context.annotation)
 * parse:132, ConfigurationClassParser (org.springframework.context.annotation)
 * processConfigBeanDefinitions:267, ConfigurationClassPostProcessor (org.springframework.context.annotation)
 * postProcessBeanDefinitionRegistry:195, ConfigurationClassPostProcessor (org.springframework.context.annotation)
 * lambda$invokeBeanDefinitionRegistryPostProcessors$26:223, PostProcessorRegistrationDelegate (org.springframework.context.support)
 * invokeBeanDefinitionRegistryPostProcessors:223, PostProcessorRegistrationDelegate (org.springframework.context.support)
 * invokeBeanFactoryPostProcessors:69, PostProcessorRegistrationDelegate (org.springframework.context.support)
 * invokeBeanFactoryPostProcessors:661, AbstractApplicationContext (org.springframework.context.support)
 * refresh:473, AbstractApplicationContext (org.springframework.context.support)
 * <init>:63, AnnotationConfigApplicationContext (org.springframework.context.annotation)
 * ImportConfig:32, App (com.goat.chapter105.item04)
 */
public class App extends BaseTest {

	/**
	 * 源码位置 ConfigurationClassParser#processImports()
	 * @Import 导入简单类，id 默认是组件的全类名
	 * ***---***	 importConfig
	 * ***---***	 com.goat.chapter105.model.Blue
	 * ***---***	 com.goat.chapter105.model.Red
	*/
	@Test
	public void ImportConfig(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ImportConfig1.class);
		look(ac);
	}

	// @Import 导入配置类  相当于xml中的 <import> 标签
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ImportConfig2.class);
		look(ac);
	}

	/**
	 * ImportSelector 导入组件，id 默认是组件的全类名
	 * ***---***	 importConfig
	 * ***---***	 com.goat.chapter105.model.Blue
	 * ***---***	 com.goat.chapter105.model.Red
	 */
	@Test
	public void ImportSelectorConfig(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ImportSelectorConfig.class);
		look(ac);
	}

	@Test
	public void ImportBeanDefinitionRegistrarConfig(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ImportBeanDefinitionRegistrarConfig.class);
		look(ac);
	}
}
