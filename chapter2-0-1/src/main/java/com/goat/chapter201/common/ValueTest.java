package com.goat.chapter201.common;

import org.springframework.beans.factory.annotation.Value;

/**
 * 	使用@Value赋值；
 * 	1、基本数值
 * 	2、可以写SpEL； #{}
 * 	3、可以写${}；取出配置文件【properties】中的值（在运行环境变量里面的值）
 */
public class ValueTest {

	@Value("山羊")
	private String name;

	@Value("#{2*11+1}")
	private Integer age;

	@Value("${ValueTest.nickName}")
	private String nickName;

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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "ValueTest{" + "name='" + name + '\'' + ", age=" + age + ", nickName='" + nickName + '\'' + '}';
	}
}
