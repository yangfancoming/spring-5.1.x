package com.goat.chapter201.bean.constructorarg;

/**
 * 测试 constructor-arg 属性
 */
public class TestConstructorArg {

	private String name;

	private String nickName;

	private int age;


	public TestConstructorArg(int age) {
		this.age = age;
		System.out.println("调用 单参 age 构造函数");
	}

	public TestConstructorArg(String name) {
		this.name = name;
		System.out.println("调用 单参 name 构造函数");
	}

	/**
	 * 记得要设定构造函数，根据 index 下标去找变量
	 * @param name	名字
	 * @param age	年龄
	 */
	public TestConstructorArg(String name, int age) {
		this.name = name;
		this.age = age;
		System.out.println("调用 双参构造函数");
	}

	public TestConstructorArg(String name, String nickName, int age) {
		this.name = name;
		this.nickName = nickName;
		this.age = age;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
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
