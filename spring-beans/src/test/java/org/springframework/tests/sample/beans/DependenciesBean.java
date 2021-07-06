

package org.springframework.tests.sample.beans;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Simple bean used to test dependency checking.
 * @since 04.09.2003
 */
public class DependenciesBean implements BeanFactoryAware {

	private int age;

	private String name;

	private TestBean spouse;

	private BeanFactory beanFactory;

	public DependenciesBean() {
		System.out.println("DependenciesBean 无参构造函数执行");
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getAge() {
		return age;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setSpouse(TestBean spouse) {
		this.spouse = spouse;
	}

	public TestBean getSpouse() {
		return spouse;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
