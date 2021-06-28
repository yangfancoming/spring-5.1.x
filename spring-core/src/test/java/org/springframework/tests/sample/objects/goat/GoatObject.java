package org.springframework.tests.sample.objects.goat;

/**
 * Created by Administrator on 2021/6/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---23:21
 */
public class GoatObject extends GoatBaseObject implements GoatSubInterface {

	private String name;

	public Integer age;

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


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
