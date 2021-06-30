package com.goat.chapter201.factoryBean;

import com.goat.chapter201.model.Red;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---17:21
 */
public class ColorFactoryBean implements FactoryBean<Red> {

	// 返回的对象就会被添加到spring容器中
	@Override
	public Red getObject() {
		System.out.println("ColorFactoryBean......getObject方法");
		return new Red();
	}

	// 对象的类型
	@Override
	public Class<?> getObjectType() {
		return Red.class;
	}

	// 是否单例
	@Override
	public boolean isSingleton() {
		return true;
	}
}
