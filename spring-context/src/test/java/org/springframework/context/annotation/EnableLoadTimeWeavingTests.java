

package org.springframework.context.annotation;

import java.lang.instrument.ClassFileTransformer;

import org.junit.Test;

import org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.instrument.classloading.LoadTimeWeaver;

import static org.mockito.BDDMockito.*;

/**
 * Unit tests for @EnableLoadTimeWeaving
 *
 * @author Chris Beams
 * @since 3.1
 */
public class EnableLoadTimeWeavingTests {

	@Test
	public void control() {
		GenericXmlApplicationContext ctx =
			new GenericXmlApplicationContext(getClass(), "EnableLoadTimeWeavingTests-context.xml");
		ctx.getBean("loadTimeWeaver", LoadTimeWeaver.class);
	}

	@Test
	public void enableLTW_withAjWeavingDisabled() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(EnableLTWConfig_withAjWeavingDisabled.class);
		ctx.refresh();
		LoadTimeWeaver loadTimeWeaver = ctx.getBean("loadTimeWeaver", LoadTimeWeaver.class);
		verifyZeroInteractions(loadTimeWeaver);
	}

	@Test
	public void enableLTW_withAjWeavingAutodetect() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(EnableLTWConfig_withAjWeavingAutodetect.class);
		ctx.refresh();
		LoadTimeWeaver loadTimeWeaver = ctx.getBean("loadTimeWeaver", LoadTimeWeaver.class);
		// no expectations -> a class file transformer should NOT be added
		// because no META-INF/aop.xml is present on the classpath
		verifyZeroInteractions(loadTimeWeaver);
	}

	@Test
	public void enableLTW_withAjWeavingEnabled() {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(EnableLTWConfig_withAjWeavingEnabled.class);
		ctx.refresh();
		LoadTimeWeaver loadTimeWeaver = ctx.getBean("loadTimeWeaver", LoadTimeWeaver.class);
		verify(loadTimeWeaver).addTransformer(isA(ClassFileTransformer.class));
	}

	@Configuration
	@EnableLoadTimeWeaving(aspectjWeaving=AspectJWeaving.DISABLED)
	static class EnableLTWConfig_withAjWeavingDisabled implements LoadTimeWeavingConfigurer {
		@Override
		public LoadTimeWeaver getLoadTimeWeaver() {
			return mock(LoadTimeWeaver.class);
		}
	}

	@Configuration
	@EnableLoadTimeWeaving(aspectjWeaving=AspectJWeaving.AUTODETECT)
	static class EnableLTWConfig_withAjWeavingAutodetect implements LoadTimeWeavingConfigurer {
		@Override
		public LoadTimeWeaver getLoadTimeWeaver() {
			return mock(LoadTimeWeaver.class);
		}
	}

	@Configuration
	@EnableLoadTimeWeaving(aspectjWeaving=AspectJWeaving.ENABLED)
	static class EnableLTWConfig_withAjWeavingEnabled implements LoadTimeWeavingConfigurer {
		@Override
		public LoadTimeWeaver getLoadTimeWeaver() {
			return mock(LoadTimeWeaver.class);
		}
	}
}
