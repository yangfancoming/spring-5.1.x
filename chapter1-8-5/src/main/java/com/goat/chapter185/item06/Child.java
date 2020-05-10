package com.goat.chapter185.item06;

/**
 * Created by Administrator on 2020/5/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/9---17:48
 */

public class Child {

	// 注意：这里并没有@Autowired注解的
	private HelloService helloService;

	private String name;
	private Integer age;

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

	public HelloService getHelloService() {
		return helloService;
	}

	public void setHelloService(HelloService helloService) {
		this.helloService = helloService;
	}
}
