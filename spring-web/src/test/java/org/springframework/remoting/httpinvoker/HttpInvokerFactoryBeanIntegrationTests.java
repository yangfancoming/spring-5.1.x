

package org.springframework.remoting.httpinvoker;

import org.junit.Test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.stereotype.Component;

import static org.junit.Assert.*;


public class HttpInvokerFactoryBeanIntegrationTests {

	@Test
	@SuppressWarnings("resource")
	public void testLoadedConfigClass() {
		ApplicationContext context = new AnnotationConfigApplicationContext(InvokerAutowiringConfig.class);
		MyBean myBean = context.getBean("myBean", MyBean.class);
		assertSame(context.getBean("myService"), myBean.myService);
		myBean.myService.handle();
		myBean.myService.handleAsync();
	}

	@Test
	@SuppressWarnings("resource")
	public void testNonLoadedConfigClass() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.registerBeanDefinition("config", new RootBeanDefinition(InvokerAutowiringConfig.class.getName()));
		context.refresh();
		MyBean myBean = context.getBean("myBean", MyBean.class);
		assertSame(context.getBean("myService"), myBean.myService);
		myBean.myService.handle();
		myBean.myService.handleAsync();
	}

	@Test
	@SuppressWarnings("resource")
	public void withConfigurationClassWithPlainFactoryBean() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(ConfigWithPlainFactoryBean.class);
		context.refresh();
		MyBean myBean = context.getBean("myBean", MyBean.class);
		assertSame(context.getBean("myService"), myBean.myService);
		myBean.myService.handle();
		myBean.myService.handleAsync();
	}


	public interface MyService {

		public void handle();

		@Async
		public void handleAsync();
	}


	@Component("myBean")
	public static class MyBean {

		@Autowired
		public MyService myService;
	}


	@Configuration
	@ComponentScan
	@Lazy
	public static class InvokerAutowiringConfig {

		@Bean
		public AsyncAnnotationBeanPostProcessor aabpp() {
			return new AsyncAnnotationBeanPostProcessor();
		}

		@Bean
		public HttpInvokerProxyFactoryBean myService() {
			HttpInvokerProxyFactoryBean factory = new HttpInvokerProxyFactoryBean();
			factory.setServiceUrl("/svc/dummy");
			factory.setServiceInterface(MyService.class);
			factory.setHttpInvokerRequestExecutor((config, invocation) -> new RemoteInvocationResult());
			return factory;
		}

		@Bean
		public FactoryBean<String> myOtherService() {
			throw new IllegalStateException("Don't ever call me");
		}
	}


	@Configuration
	static class ConfigWithPlainFactoryBean {

		@Autowired
		Environment env;

		@Bean
		public MyBean myBean() {
			return new MyBean();
		}

		@Bean
		public HttpInvokerProxyFactoryBean myService() {
			String name = env.getProperty("testbean.name");
			HttpInvokerProxyFactoryBean factory = new HttpInvokerProxyFactoryBean();
			factory.setServiceUrl("/svc/" + name);
			factory.setServiceInterface(MyService.class);
			factory.setHttpInvokerRequestExecutor((config, invocation) -> new RemoteInvocationResult());
			return factory;
		}
	}

}
