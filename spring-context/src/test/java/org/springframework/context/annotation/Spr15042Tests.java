

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.CommonsPool2TargetSource;


public class Spr15042Tests {

	@Test
	public void poolingTargetSource() {
		new AnnotationConfigApplicationContext(PoolingTargetSourceConfig.class);
	}


	@Configuration
	static class PoolingTargetSourceConfig {

		@Bean
		@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
		public ProxyFactoryBean myObject() {
			ProxyFactoryBean pfb = new ProxyFactoryBean();
			pfb.setTargetSource(poolTargetSource());
			return pfb;
		}

		@Bean
		public CommonsPool2TargetSource poolTargetSource() {
			CommonsPool2TargetSource pool = new CommonsPool2TargetSource();
			pool.setMaxSize(3);
			pool.setTargetBeanName("myObjectTarget");
			return pool;
		}

		@Bean(name = "myObjectTarget")
		@Scope(scopeName = "prototype")
		public Object myObjectTarget() {
			return new Object();
		}
	}

}
