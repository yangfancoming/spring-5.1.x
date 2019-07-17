

package org.springframework.aop.config;

import java.util.List;

import org.w3c.dom.Node;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionDecorator;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Base implementation for
 * {@link org.springframework.beans.factory.xml.BeanDefinitionDecorator BeanDefinitionDecorators}
 * wishing to add an {@link org.aopalliance.intercept.MethodInterceptor interceptor}
 * to the resulting bean.
 *
 * <p>This base class controls the creation of the {@link ProxyFactoryBean} bean definition
 * and wraps the original as an inner-bean definition for the {@code target} property
 * of {@link ProxyFactoryBean}.
 *
 * <p>Chaining is correctly handled, ensuring that only one {@link ProxyFactoryBean} definition
 * is created. If a previous {@link org.springframework.beans.factory.xml.BeanDefinitionDecorator}
 * already created the {@link org.springframework.aop.framework.ProxyFactoryBean} then the
 * interceptor is simply added to the existing definition.
 *
 * <p>Subclasses have only to create the {@code BeanDefinition} to the interceptor that
 * they wish to add.
 *
 * @author Rob Harrop

 * @since 2.0
 * @see org.aopalliance.intercept.MethodInterceptor
 */
public abstract class AbstractInterceptorDrivenBeanDefinitionDecorator implements BeanDefinitionDecorator {

	@Override
	public final BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definitionHolder, ParserContext parserContext) {
		BeanDefinitionRegistry registry = parserContext.getRegistry();

		// get the root bean name - will be the name of the generated proxy factory bean
		String existingBeanName = definitionHolder.getBeanName();
		BeanDefinition targetDefinition = definitionHolder.getBeanDefinition();
		BeanDefinitionHolder targetHolder = new BeanDefinitionHolder(targetDefinition, existingBeanName + ".TARGET");

		// delegate to subclass for interceptor definition
		BeanDefinition interceptorDefinition = createInterceptorDefinition(node);

		// generate name and register the interceptor
		String interceptorName = existingBeanName + '.' + getInterceptorNameSuffix(interceptorDefinition);
		BeanDefinitionReaderUtils.registerBeanDefinition(
				new BeanDefinitionHolder(interceptorDefinition, interceptorName), registry);

		BeanDefinitionHolder result = definitionHolder;

		if (!isProxyFactoryBeanDefinition(targetDefinition)) {
			// create the proxy definition
			RootBeanDefinition proxyDefinition = new RootBeanDefinition();
			// create proxy factory bean definition
			proxyDefinition.setBeanClass(ProxyFactoryBean.class);
			proxyDefinition.setScope(targetDefinition.getScope());
			proxyDefinition.setLazyInit(targetDefinition.isLazyInit());
			// set the target
			proxyDefinition.setDecoratedDefinition(targetHolder);
			proxyDefinition.getPropertyValues().add("target", targetHolder);
			// create the interceptor names list
			proxyDefinition.getPropertyValues().add("interceptorNames", new ManagedList<String>());
			// copy autowire settings from original bean definition.
			proxyDefinition.setAutowireCandidate(targetDefinition.isAutowireCandidate());
			proxyDefinition.setPrimary(targetDefinition.isPrimary());
			if (targetDefinition instanceof AbstractBeanDefinition) {
				proxyDefinition.copyQualifiersFrom((AbstractBeanDefinition) targetDefinition);
			}
			// wrap it in a BeanDefinitionHolder with bean name
			result = new BeanDefinitionHolder(proxyDefinition, existingBeanName);
		}

		addInterceptorNameToList(interceptorName, result.getBeanDefinition());
		return result;
	}

	@SuppressWarnings("unchecked")
	private void addInterceptorNameToList(String interceptorName, BeanDefinition beanDefinition) {
		List<String> list = (List<String>) beanDefinition.getPropertyValues().get("interceptorNames");
		Assert.state(list != null, "Missing 'interceptorNames' property");
		list.add(interceptorName);
	}

	private boolean isProxyFactoryBeanDefinition(BeanDefinition existingDefinition) {
		return ProxyFactoryBean.class.getName().equals(existingDefinition.getBeanClassName());
	}

	protected String getInterceptorNameSuffix(BeanDefinition interceptorDefinition) {
		String beanClassName = interceptorDefinition.getBeanClassName();
		return (StringUtils.hasLength(beanClassName) ?
				StringUtils.uncapitalize(ClassUtils.getShortName(beanClassName)) : "");
	}

	/**
	 * Subclasses should implement this method to return the {@code BeanDefinition}
	 * for the interceptor they wish to apply to the bean being decorated.
	 */
	protected abstract BeanDefinition createInterceptorDefinition(Node node);

}
