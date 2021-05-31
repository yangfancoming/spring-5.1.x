package com.goat.chapter185.common;

/**
 * Created by Administrator on 2021/5/30.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/5/30---20:34
 */
public class Person {

	private String name;
	private Integer age;

	public Person() {
		System.out.println("无参构造");
	}

	public Person(String name) {
		System.out.println("单参构造 name");
		this.name = name;
	}

	public Person(Integer age) {
		System.out.println("单参构造 age");
		this.age = age;
	}

	public Person(String name, Integer age) {
		System.out.println("双参构造");
		this.name = name;
		this.age = age;
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

	public void say(){
		System.out.println("hello");
	}
}
