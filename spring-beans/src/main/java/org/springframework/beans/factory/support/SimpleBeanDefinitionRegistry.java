

package org.springframework.beans.factory.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Simple implementation of the {@link BeanDefinitionRegistry} interface.
 * Provides registry capabilities only, with no factory capabilities built in. （built in --- 内置）
 * Can for example be used for testing bean definition readers.
 * @since 2.5.2
 */
public class SimpleBeanDefinitionRegistry extends SimpleAliasRegistry implements BeanDefinitionRegistry {

	/** Map of bean definition objects, keyed by bean name. */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(64);

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinitionRegistry】 interface
	//---------------------------------------------------------------------
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
		Assert.hasText(beanName, "'beanName' must not be empty");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		logger.warn("【IOC容器中 添加BeanDefinition 内存态  --- 新建方式 】 beanName： " + beanName);
		beanDefinitionMap.put(beanName, beanDefinition);
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		if (beanDefinitionMap.remove(beanName) == null) {
			throw new NoSuchBeanDefinitionException(beanName);
		}
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition bd = beanDefinitionMap.get(beanName);
		if (bd == null) throw new NoSuchBeanDefinitionException(beanName);
		return bd;
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return beanDefinitionMap.containsKey(beanName);
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return StringUtils.toStringArray(beanDefinitionMap.keySet());
	}

	@Override
	public int getBeanDefinitionCount() {
		return beanDefinitionMap.size();
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		return isAlias(beanName) || containsBeanDefinition(beanName);
	}
}
