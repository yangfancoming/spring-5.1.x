package org.springframework.test;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2020/5/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/9---17:07
 */
public class App {

	@Test
	public void test(){

	}

	@Test
	public void schedulerRepositoryExposure() {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("org/springframework/scheduling/quartz/test.xml");
//		Assert.assertEquals(SchedulerRepository.getInstance().lookup("myScheduler"),ctx.getBean("scheduler"));
		Object jobFactory = ctx.getBean("jobFactory");
		System.out.println(jobFactory);
	}
}
