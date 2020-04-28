package com.goat.chapter185.item05;


import org.springframework.beans.factory.FactoryBean;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: 家具工厂bean
 * @ author  山羊来了
 * @ date 2019/8/17---19:49
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