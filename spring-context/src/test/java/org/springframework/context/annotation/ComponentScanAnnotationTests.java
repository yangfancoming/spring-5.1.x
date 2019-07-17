

package org.springframework.context.annotation;

import org.junit.Test;

import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Unit tests for the @ComponentScan annotation.

 * @since 3.1
 * @see ComponentScanAnnotationIntegrationTests
 */
public class ComponentScanAnnotationTests {

	@Test
	public void noop() {
		// no-op; the @ComponentScan-annotated MyConfig class below simply exercises
		// available attributes of the annotation.
	}
}


@interface MyAnnotation {
}

@Configuration
@ComponentScan(
	basePackageClasses = TestBean.class,
	nameGenerator = DefaultBeanNameGenerator.class,
	scopedProxy = ScopedProxyMode.NO,
	scopeResolver = AnnotationScopeMetadataResolver.class,
	resourcePattern = "**/*custom.class",
	useDefaultFilters = false,
	includeFilters = {
		@Filter(type = FilterType.ANNOTATION, value = MyAnnotation.class)
	},
	excludeFilters = {
		@Filter(type = FilterType.CUSTOM, value = TypeFilter.class)
	},
	lazyInit = true
)
class MyConfig {
}

@ComponentScan(basePackageClasses = example.scannable.NamedComponent.class)
class SimpleConfig {
}
