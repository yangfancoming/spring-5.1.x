

package org.springframework.aop.aspectj;

import org.junit.Test;

import org.springframework.aop.aspectj.AspectJAdviceParameterNameDiscoverer.AmbiguousBindingException;

/**
 * Tests just the annotation binding part of {@link AspectJAdviceParameterNameDiscoverer};
 * see supertype for remaining tests.
 *
 * @author Adrian Colyer
 * @author Chris Beams
 */
public class TigerAspectJAdviceParameterNameDiscovererTests extends AspectJAdviceParameterNameDiscovererTests {

	@Test
	public void testAtThis() {
		assertParameterNames(getMethod("oneAnnotation"),"@this(a)", new String[] {"a"});
	}

	@Test
	public void testAtTarget() {
		assertParameterNames(getMethod("oneAnnotation"),"@target(a)", new String[] {"a"});
	}

	@Test
	public void testAtArgs() {
		assertParameterNames(getMethod("oneAnnotation"),"@args(a)", new String[] {"a"});
	}

	@Test
	public void testAtWithin() {
		assertParameterNames(getMethod("oneAnnotation"),"@within(a)", new String[] {"a"});
	}

	@Test
	public void testAtWithincode() {
		assertParameterNames(getMethod("oneAnnotation"),"@withincode(a)", new String[] {"a"});
	}

	@Test
	public void testAtAnnotation() {
		assertParameterNames(getMethod("oneAnnotation"),"@annotation(a)", new String[] {"a"});
	}

	@Test
	public void testAmbiguousAnnotationTwoVars() {
		assertException(getMethod("twoAnnotations"),"@annotation(a) && @this(x)", AmbiguousBindingException.class,
				"Found 2 potential annotation variable(s), and 2 potential argument slots");
	}

	@Test
	public void testAmbiguousAnnotationOneVar() {
		assertException(getMethod("oneAnnotation"),"@annotation(a) && @this(x)",IllegalArgumentException.class,
				"Found 2 candidate annotation binding variables but only one potential argument binding slot");
	}

	@Test
	public void testAnnotationMedley() {
		assertParameterNames(getMethod("annotationMedley"),"@annotation(a) && args(count) && this(foo)",
				null, "ex", new String[] {"ex", "foo", "count", "a"});
	}


	public void oneAnnotation(MyAnnotation ann) {}

	public void twoAnnotations(MyAnnotation ann, MyAnnotation anotherAnn) {}

	public void annotationMedley(Throwable t, Object foo, int x, MyAnnotation ma) {}

	@interface MyAnnotation {}

}
