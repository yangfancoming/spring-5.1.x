

package org.springframework.context.annotation;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Delegate factory class used to just introduce an AOP framework dependency
 * when actually creating a scoped proxy.
 *

 * @since 3.0
 * @see org.springframework.aop.scope.ScopedProxyUtils#createScopedProxy
 */
final class ScopedProxyCreator {

	private ScopedProxyCreator() {
	}


	public static BeanDefinitionHolder createScopedProxy(
			BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry, boolean proxyTargetClass) {

		return ScopedProxyUtils.createScopedProxy(definitionHolder, registry, proxyTargetClass);
	}

	public static String getTargetBeanName(String originalBeanName) {
		return ScopedProxyUtils.getTargetBeanName(originalBeanName);
	}

}
