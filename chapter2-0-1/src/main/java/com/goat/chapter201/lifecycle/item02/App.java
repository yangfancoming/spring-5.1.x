package com.goat.chapter201.lifecycle.item02;

import com.goat.chapter201.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/5/6.
 * @ Description:
 * @ author  山羊来了
 * @ date 2020/5/6---20:59
 */
public class App {



	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		Arrays.stream(ac.getBeanDefinitionNames()).forEach(x->System.out.println("***---***	 " + x));
	}

	@Test
	public void test1(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		Baby baby = ac.getBean(Baby.class);
		baby.hello();

		Daddy daddy = ac.getBean(Daddy.class);
		daddy.dosomething();
	}

	@Test
	public void test11(){
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("beans11.xml");
		Person person = ac.getBean(Person.class);
		Assert.assertEquals("明明",person.getName());
		Assert.assertEquals(0,person.getAge());
	}


	@Test
	public void test2(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(MyBeanDefinitionRegistryPostProcessor1.class);
		Arrays.stream(ac.getBeanDefinitionNames()).forEach(x->System.out.println("***---***	 " + x));
	}

	@Test
	public void test3(){
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("beans2.xml");
		Person person = ac.getBean(Person.class);
//		Assert.assertEquals("jordan",person.getName());
		Assert.assertEquals("pipen",person.getName());
		Assert.assertEquals(23,person.getAge());
	}
}
