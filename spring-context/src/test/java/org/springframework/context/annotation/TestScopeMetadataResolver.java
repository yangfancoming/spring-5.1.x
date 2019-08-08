

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinition;


public class TestScopeMetadataResolver implements ScopeMetadataResolver {

	@Override
	public ScopeMetadata resolveScopeMetadata(BeanDefinition beanDefinition) {
		ScopeMetadata metadata = new ScopeMetadata();
		metadata.setScopeName("myCustomScope");
		return metadata;
	}

}
