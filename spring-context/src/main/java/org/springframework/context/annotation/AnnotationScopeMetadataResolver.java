

package org.springframework.context.annotation;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;

/**
 * A {@link ScopeMetadataResolver} implementation that by default checks for the presence of Spring's {@link Scope @Scope} annotation on the bean class.
 * The exact type of annotation that is checked for is configurable via {@link #setScopeAnnotationType(Class)}.
 * @since 2.5
 * @see org.springframework.context.annotation.Scope
 */
public class AnnotationScopeMetadataResolver implements ScopeMetadataResolver {

	private final ScopedProxyMode defaultProxyMode;

	protected Class<? extends Annotation> scopeAnnotationType = Scope.class;

	/**
	 * Construct a new {@code AnnotationScopeMetadataResolver}.
	 * @see #AnnotationScopeMetadataResolver(ScopedProxyMode)
	 * @see ScopedProxyMode#NO
	 */
	public AnnotationScopeMetadataResolver() {
		this.defaultProxyMode = ScopedProxyMode.NO;
	}

	/**
	 * Construct a new {@code AnnotationScopeMetadataResolver} using the
	 * supplied default {@link ScopedProxyMode}.
	 * @param defaultProxyMode the default scoped-proxy mode
	 */
	public AnnotationScopeMetadataResolver(ScopedProxyMode defaultProxyMode) {
		Assert.notNull(defaultProxyMode, "'defaultProxyMode' must not be null");
		this.defaultProxyMode = defaultProxyMode;
	}

	/**
	 * Set the type of annotation that is checked for by this
	 * {@code AnnotationScopeMetadataResolver}.
	 * @param scopeAnnotationType the target annotation type
	 */
	public void setScopeAnnotationType(Class<? extends Annotation> scopeAnnotationType) {
		Assert.notNull(scopeAnnotationType, "'scopeAnnotationType' must not be null");
		this.scopeAnnotationType = scopeAnnotationType;
	}

	@Override
	public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
		ScopeMetadata metadata = new ScopeMetadata();
		if (definition instanceof AnnotatedBeanDefinition) {
			AnnotatedBeanDefinition annDef = (AnnotatedBeanDefinition) definition;
			AnnotationAttributes attributes = AnnotationConfigUtils.attributesFor(annDef.getMetadata(), scopeAnnotationType);
			if (attributes != null) {
				metadata.setScopeName(attributes.getString("value"));
				ScopedProxyMode proxyMode = attributes.getEnum("proxyMode");
				if (proxyMode == ScopedProxyMode.DEFAULT) {
					proxyMode = defaultProxyMode;
				}
				metadata.setScopedProxyMode(proxyMode);
			}
		}
		return metadata;
	}

}
