

package org.springframework.aop.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.Test;

/**
 * Additional parameter name discover tests that need Java 5.
 * Yes this will re-run the tests from the superclass, but that
 * doesn't matter in the grand scheme of things...
 *
 * @author Adrian Colyer

 */
public class AspectJAdviceParameterNameDiscoverAnnotationTests extends AspectJAdviceParameterNameDiscovererTests {

	@Test
	public void testAnnotationBinding() {
		assertParameterNames(getMethod("pjpAndAnAnnotation"),
				"execution(* *(..)) && @annotation(ann)",
				new String[] {"thisJoinPoint","ann"});
	}


	public void pjpAndAnAnnotation(ProceedingJoinPoint pjp, MyAnnotation ann) {}

	@interface MyAnnotation {}

}
