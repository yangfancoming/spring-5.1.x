

package org.springframework.aop.aspectj.autoproxy;

import java.io.IOException;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Test;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.sample.beans.ITestBean;

import static org.junit.Assert.*;

/**
 * @author Rob Harrop

 * @since 2.0
 */
public class AtAspectJAfterThrowingTests {

	@Test
	public void testAccessThrowable() throws Exception {
		ClassPathXmlApplicationContext ctx =
			new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());

		ITestBean bean = (ITestBean) ctx.getBean("testBean");
		ExceptionHandlingAspect aspect = (ExceptionHandlingAspect) ctx.getBean("aspect");

		assertTrue(AopUtils.isAopProxy(bean));
		try {
			bean.unreliableFileOperation();
		}
		catch (IOException e) {
			//
		}

		assertEquals(1, aspect.handled);
		assertNotNull(aspect.lastException);
	}
}


@Aspect
class ExceptionHandlingAspect {

	public int handled;

	public IOException lastException;

	@AfterThrowing(pointcut = "within(org.springframework.tests.sample.beans.ITestBean+)", throwing = "ex")
	public void handleIOException(IOException ex) {
		handled++;
		lastException = ex;
	}

}
