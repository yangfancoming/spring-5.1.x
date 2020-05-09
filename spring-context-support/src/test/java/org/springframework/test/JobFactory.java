package org.springframework.test;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * Created by Administrator on 2020/5/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/9---17:07
 */
public class JobFactory extends AdaptableJobFactory {

	@Autowired
	private AutowireCapableBeanFactory capableBeanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		//调用父类的方法
		Object jobInstance = super.createJobInstance(bundle);
		//进行注入
		capableBeanFactory.autowireBean(jobInstance);
		return jobInstance;
	}
}