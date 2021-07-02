package com.goat.chapter201.lifecycle.item01;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:43
 */
public class EnvironmentAwareTest implements EnvironmentAware {
	@Override
	public void setEnvironment(Environment environment) {
		System.out.println("setEnvironment..." + environment);
	}
}
