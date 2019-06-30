

package org.springframework.test.context.junit4.annotation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit test suite for annotation-driven <em>configuration class</em>
 * support in the Spring TestContext Framework.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@RunWith(Suite.class)
// Note: the following 'multi-line' layout is for enhanced code readability.
@SuiteClasses({//
AnnotationConfigSpringJUnit4ClassRunnerAppCtxTests.class,//
	DefaultConfigClassesBaseTests.class,//
	DefaultConfigClassesInheritedTests.class,//
	BeanOverridingDefaultConfigClassesInheritedTests.class,//
	ExplicitConfigClassesBaseTests.class,//
	ExplicitConfigClassesInheritedTests.class,//
	BeanOverridingExplicitConfigClassesInheritedTests.class,//
	DefaultLoaderDefaultConfigClassesBaseTests.class,//
	DefaultLoaderDefaultConfigClassesInheritedTests.class,//
	DefaultLoaderBeanOverridingDefaultConfigClassesInheritedTests.class,//
	DefaultLoaderExplicitConfigClassesBaseTests.class,//
	DefaultLoaderExplicitConfigClassesInheritedTests.class,//
	DefaultLoaderBeanOverridingExplicitConfigClassesInheritedTests.class //
})
public class AnnotationConfigTestSuite {
}
