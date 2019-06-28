

package org.springframework.cache.config;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * @author Costin Leau
 * @author Chris Beams
 */
public class AnnotationDrivenCacheConfigTests extends AbstractCacheAnnotationTests {

	@Override
	protected ConfigurableApplicationContext getApplicationContext() {
		return new GenericXmlApplicationContext(
				"/org/springframework/cache/config/annotationDrivenCacheConfig.xml");
	}

}
