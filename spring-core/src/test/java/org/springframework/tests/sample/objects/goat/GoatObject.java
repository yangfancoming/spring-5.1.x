package org.springframework.tests.sample.objects.goat;

/**
 * Created by Administrator on 2021/6/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---23:21
 */
public class GoatObject implements GoatSubInterface {

	public String mySelf(){
		return "self";
	}

	@Override
	public String interfaceAbstract() {
		return "abstract-class";
	}

	// default 方法为 可选实现
	@Override
	public String interfaceDefault() {
		return "default-class";
	}
}
