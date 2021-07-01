package com.goat.chapter201.bean.item02;

/**
 * Created by Administrator on 2021/7/1.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/1---17:09
 */
public class RootBean {

	private String name;
	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "RootBean{" + "name='" + name + '\'' + ", age='" + age + '\'' + '}';
	}
}

