package com.goat.chapter185.item05;


import org.springframework.beans.factory.FactoryBean;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: 家具工厂bean
 * @ author  山羊来了
 * @ date 2019/8/17---19:49
 *
 * 这是个特殊的 Bean 他是个工厂 Bean，可以产生 Bean 的 Bean
 *
 * 一般情况下，Spring通过反射机制利用<bean>的class属性指定实现类实例化Bean，
 * 在某些情况下，实例化Bean过程比较复杂，如果按照传统的方式，则需要在<bean>中提供大量的配置信息。
 * 配置方式的灵活性是受限的，这时采用编码的方式可能会得到一个简单的方案。 因此FactoryBean<T> 接口应运而生！
 * Spring为此提供了一个org.springframework.bean.factory.FactoryBean的工厂类接口，用户可以通过实现该接口定制实例化Bean的逻辑。
 */
public class FurnitureFactoryBean implements FactoryBean<Furniture> {

	private String furniture;

	public void setFurniture(String furniture) {
		this.furniture = furniture;
	}

	// 这个Bean是我们自己new的，这里我们就可以控制Bean的创建过程了
	@Override
	public Furniture getObject()  {
		if (null == furniture) throw new IllegalArgumentException("'furniture' is required");
		if ("chair".equals(furniture)) {
			return new Chair();
		} else if ("desk".equals(furniture)) {
			return new Desk();
		} else {
			throw new IllegalArgumentException("'furniture' type error");
		}
	}

	@Override
	public Class<?> getObjectType() {
		if (null == furniture) throw new IllegalArgumentException("'furniture' is required");
		if ("chair".equals(furniture)) {
			return Chair.class;
		} else if ("desk".equals(furniture)) {
			return Desk.class;
		} else {
			throw new IllegalArgumentException("'furniture' type error");
		}
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}