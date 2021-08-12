package com.goat.chapter201.cyclicdependency.item04;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/3---15:26
 */
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
public class B {
	@Autowired
	private A a;
}