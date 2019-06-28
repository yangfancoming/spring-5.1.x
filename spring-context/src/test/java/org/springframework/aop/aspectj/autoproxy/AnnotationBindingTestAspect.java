

package org.springframework.aop.aspectj.autoproxy;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author Adrian Colyer
 */
class AnnotationBindingTestAspect {

	public String doWithAnnotation(ProceedingJoinPoint pjp, TestAnnotation testAnnotation) throws Throwable {
		return testAnnotation.value();
	}

}
