package com.goat.chapter185.item05;

import com.goat.chapter185.common.Dog;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

import static org.springframework.core.env.AbstractEnvironment.DEFAULT_PROFILES_PROPERTY_NAME;

public class App {

	private final ConfigurableEnvironment environment = new StandardEnvironment();

	@Test
	public void test(){
		System.setProperty(DEFAULT_PROFILES_PROPERTY_NAME, "${spring.profiles.default}");
		String[] defaultProfiles = environment.getDefaultProfiles();
		System.out.println(defaultProfiles);
	}

	@Test
	public void testProfile() {
		common("test");
	}

	@Test
	public void proProfile() {
		common("pro");
	}

	@Test
	public void devProfile() {
		common("dev");
	}

	private void common(String profile){
		System.setProperty(DEFAULT_PROFILES_PROPERTY_NAME, profile); // profile激活方式一
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean4.xml");
		Dog dog = applicationContext.getBean("dog", Dog.class);
		dog.sayHello();
	}
}
