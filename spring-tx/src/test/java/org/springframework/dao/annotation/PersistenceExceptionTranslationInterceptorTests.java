

package org.springframework.dao.annotation;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.support.PersistenceExceptionTranslationInterceptor;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.stereotype.Repository;

/**
 * Tests for standalone usage of a PersistenceExceptionTranslationInterceptor, as explicit advice bean in a BeanFactory
 * rather than applied as part of a PersistenceExceptionTranslationAdvisor.
 *

 */
public class PersistenceExceptionTranslationInterceptorTests extends PersistenceExceptionTranslationAdvisorTests {

	@Override
	protected void addPersistenceExceptionTranslation(ProxyFactory pf, PersistenceExceptionTranslator pet) {
		if (AnnotationUtils.findAnnotation(pf.getTargetClass(), Repository.class) != null) {
			DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
			bf.registerBeanDefinition("peti", new RootBeanDefinition(PersistenceExceptionTranslationInterceptor.class));
			bf.registerSingleton("pet", pet);
			pf.addAdvice((PersistenceExceptionTranslationInterceptor) bf.getBean("peti"));
		}
	}

}
