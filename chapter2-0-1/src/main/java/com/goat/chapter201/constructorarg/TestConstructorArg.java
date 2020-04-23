package com.goat.chapter201.constructorarg;

/**
 * 测试 constructor-arg 属性
 */
public class TestConstructorArg {

	private String name;

	private int age;
	/**
	 * 记得要设定构造函数，根据 index 下标去找变量
	 * @param name	名字
	 * @param age	年龄
	 */
	public TestConstructorArg(String name, int age) {
		this.name = name;
		this.age = age;
	}

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
		return "TestConstructorArg{" + "name='" + name + '\'' + ", age=" + age + '}';
	}
}
