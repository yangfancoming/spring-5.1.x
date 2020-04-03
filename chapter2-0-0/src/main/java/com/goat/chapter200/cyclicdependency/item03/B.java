package com.goat.chapter200.cyclicdependency.item03;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/3---15:26
 */
@Service
public class B {
	@Autowired
	private A a;
}