

package org.springframework.scheduling.annotation;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.Nullable;

/**
 * A convenience {@link AsyncConfigurer} that implements all methods
 * so that the defaults are used. Provides a backward compatible alternative
 * of implementing {@link AsyncConfigurer} directly.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class AsyncConfigurerSupport implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		return null;
	}

	@Override
	@Nullable
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return null;
	}

}
