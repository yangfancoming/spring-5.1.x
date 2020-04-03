

package org.springframework.scheduling.quartz;

import java.text.ParseException;

import org.junit.Test;
import org.quartz.CronTrigger;

import static org.junit.Assert.*;


public class CronTriggerFactoryBeanTests {

	@Test
	public void createWithoutJobDetail() throws ParseException {
		CronTriggerFactoryBean factory = new CronTriggerFactoryBean();
		factory.setName("myTrigger");
		factory.setCronExpression("0 15 10 ? * *");
		factory.afterPropertiesSet();
		CronTrigger trigger = factory.getObject();
		assertEquals("0 15 10 ? * *", trigger.getCronExpression());
	}

}
