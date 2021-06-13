package com.goat.chapter185.item08;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2021/6/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/12---18:52
 */
@Component
public class InstantiationAwareBeanPostProcessor1 implements InstantiationAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...Before	" + beanName );
		if (beanClass == Daddy.class){
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(beanClass);
			enhancer.setCallback(new MyMethodInterceptor());
			Daddy daddy = (Daddy)enhancer.create(); // 这里创建了对象 走Daddy的无参构造函数
			System.out.print("返回动态代理\n");
			return daddy; // 这里返回非空，则直接调用 BeanPostProcessor#postProcessAfterInitialization()
		}
		return null; // 返回空，则走正常流水线创建和初始化bean
	}

	/**
	 * 如果返回false，不但本接口内的postProcessProperties接口不会执行，
	 * 后续的BeanFactoryPostProcessor接口中postProcessBeanFactory方法设置的属性也将不会生效。
	 * @see BeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	*/
	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...After	" + beanName );
		return true; //  返回true会调用postProcessProperties()  否则不会调用
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...postProcessProperties	" + beanName );
		if(bean instanceof Baby){
			// 修改bean中a 的属性值
			PropertyValue value = pvs.getPropertyValue("age");
			value.setConvertedValue(100);
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...postProcessProperties	 修改属性！" + beanName );
			return pvs;
		}
		return null;
	}
}
