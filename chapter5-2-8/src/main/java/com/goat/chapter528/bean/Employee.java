package com.goat.chapter528.bean;

/**
 * Created by 64274 on 2019/8/14.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/14---20:08
 */
public class Employee {

	private String name;
	private int age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "Employee{" + "name='" + name + '\'' + ", age=" + age + '}';
	}
}
