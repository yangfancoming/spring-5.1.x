package com.goat.chapter201.lifecycle.item02;

/**
 * Created by Administrator on 2021/6/13.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/13---15:07
 */
public class Daddy {

	private String name;
	private Integer age;

	public Daddy() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ Daddy 无参构造函数 执行");
	}

	public void dosomething() {
		System.out.print("@@@@@@@@@@@@@@@@@@@@@@@ Daddy 执行了dosomething.......\n" + name + age);
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
