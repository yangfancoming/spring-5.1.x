

package org.springframework.context.annotation;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import static org.junit.Assert.*;
import static org.springframework.context.annotation.ScopedProxyMode.*;

/**
 * Unit tests for {@link AnnotationScopeMetadataResolver}.
 */
public class AnnotationScopeMetadataResolverTests {

	private AnnotationScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

	@Test
	public void resolveScopeMetadataShouldNotApplyScopedProxyModeToSingleton() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(AnnotatedWithSingletonScope.class);
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals(BeanDefinition.SCOPE_SINGLETON, scopeMetadata.getScopeName());
		assertEquals(NO, scopeMetadata.getScopedProxyMode());
	}

	@Test
	public void resolveScopeMetadataShouldApplyScopedProxyModeToPrototype() {
		this.scopeMetadataResolver = new AnnotationScopeMetadataResolver(INTERFACES);
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(AnnotatedWithPrototypeScope.class);
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals(BeanDefinition.SCOPE_PROTOTYPE, scopeMetadata.getScopeName());
		assertEquals(INTERFACES, scopeMetadata.getScopedProxyMode());
	}

	@Test
	public void resolveScopeMetadataShouldReadScopedProxyModeFromAnnotation() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(AnnotatedWithScopedProxy.class);
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals("request", scopeMetadata.getScopeName());
		assertEquals(TARGET_CLASS, scopeMetadata.getScopedProxyMode());
	}

	@Test
	public void customRequestScope() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(AnnotatedWithCustomRequestScope.class);
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals("request", scopeMetadata.getScopeName());
		assertEquals(NO, scopeMetadata.getScopedProxyMode());
	}

	@Test
	public void customRequestScopeViaAsm() throws IOException {
		MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
		MetadataReader reader = readerFactory.getMetadataReader(AnnotatedWithCustomRequestScope.class.getName());
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(reader.getAnnotationMetadata());
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals("request", scopeMetadata.getScopeName());
		assertEquals(NO, scopeMetadata.getScopedProxyMode());
	}

	@Test
	public void customRequestScopeWithAttribute() {
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(
			AnnotatedWithCustomRequestScopeWithAttributeOverride.class);
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals("request", scopeMetadata.getScopeName());
		assertEquals(TARGET_CLASS, scopeMetadata.getScopedProxyMode());
	}

	@Test
	public void customRequestScopeWithAttributeViaAsm() throws IOException {
		MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory();
		MetadataReader reader = readerFactory.getMetadataReader(AnnotatedWithCustomRequestScopeWithAttributeOverride.class.getName());
		AnnotatedBeanDefinition bd = new AnnotatedGenericBeanDefinition(reader.getAnnotationMetadata());
		ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(bd);
		assertNotNull("resolveScopeMetadata(..) must *never* return null.", scopeMetadata);
		assertEquals("request", scopeMetadata.getScopeName());
		assertEquals(TARGET_CLASS, scopeMetadata.getScopedProxyMode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ctorWithNullScopedProxyMode() {
		new AnnotationScopeMetadataResolver(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setScopeAnnotationTypeWithNullType() {
		scopeMetadataResolver.setScopeAnnotationType(null);
	}


	@Retention(RetentionPolicy.RUNTIME)
	@Scope("request")
	@interface CustomRequestScope {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Scope("request")
	@interface CustomRequestScopeWithAttributeOverride {

		ScopedProxyMode proxyMode();
	}

	@Scope("singleton")
	private static class AnnotatedWithSingletonScope {
	}

	@Scope("prototype")
	private static class AnnotatedWithPrototypeScope {
	}

	@Scope(scopeName = "request", proxyMode = TARGET_CLASS)
	private static class AnnotatedWithScopedProxy {
	}

	@CustomRequestScope
	private static class AnnotatedWithCustomRequestScope {
	}

	@CustomRequestScopeWithAttributeOverride(proxyMode = TARGET_CLASS)
	private static class AnnotatedWithCustomRequestScopeWithAttributeOverride {
	}
}
