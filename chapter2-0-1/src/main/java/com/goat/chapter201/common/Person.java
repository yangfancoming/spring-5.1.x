package com.goat.chapter201.common;

/**
 * Created by 64274 on 2019/8/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/12---20:33
 */

public class Person {

	public Person() {
		System.out.println("Person 构造函数执行");
	}

	private String name;
	private int age;
	private int phone;

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

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "Person{" + "name='" + name + '\'' + ", age=" + age + ", phone=" + phone + '}';
	}
}