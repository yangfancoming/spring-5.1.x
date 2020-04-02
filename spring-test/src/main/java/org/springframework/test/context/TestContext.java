

package org.springframework.test.context;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;

/**
 * {@code TestContext} encapsulates the context in which a test is executed,
 * agnostic of the actual testing framework in use.
 *
 * As of Spring Framework 5.0, concrete implementations are highly encouraged
 * to implement a <em>copy constructor</em> in order to allow the immutable state
 * and attributes of a {@code TestContext} to be used as a template for additional
 * contexts created for parallel test execution. The copy constructor must accept a
 * single argument of the type of the concrete implementation. Any implementation
 * that does not provide a copy constructor will likely fail in an environment
 * that executes tests concurrently.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see TestContextManager
 * @see TestExecutionListener
 */
public interface TestContext extends AttributeAccessor, Serializable {

	/**
	 * Get the {@linkplain ApplicationContext application context} for this
	 * test context, possibly cached.
	 * Implementations of this method are responsible for loading the
	 * application context if the corresponding context has not already been
	 * loaded, potentially caching the context as well.
	 * @return the application context (never {@code null})
	 * @throws IllegalStateException if an error occurs while retrieving the
	 * application context
	 */
	ApplicationContext getApplicationContext();

	/**
	 * Get the {@linkplain Class test class} for this test context.
	 * @return the test class (never {@code null})
	 */
	Class<?> getTestClass();

	/**
	 * Get the current {@linkplain Object test instance} for this test context.
	 * Note: this is a mutable property.
	 * @return the current test instance (never {@code null})
	 * @see #updateState(Object, Method, Throwable)
	 */
	Object getTestInstance();

	/**
	 * Get the current {@linkplain Method test method} for this test context.
	 * Note: this is a mutable property.
	 * @return the current test method (never {@code null})
	 * @see #updateState(Object, Method, Throwable)
	 */
	Method getTestMethod();

	/**
	 * Get the {@linkplain Throwable exception} that was thrown during execution
	 * of the {@linkplain #getTestMethod() test method}.
	 * Note: this is a mutable property.
	 * @return the exception that was thrown, or {@code null} if no exception was thrown
	 * @see #updateState(Object, Method, Throwable)
	 */
	@Nullable
	Throwable getTestException();

	/**
	 * Call this method to signal that the {@linkplain ApplicationContext application
	 * context} associated with this test context is <em>dirty</em> and should be
	 * removed from the context cache.
	 * Do this if a test has modified the context ; for example, by
	 * modifying the state of a singleton bean, modifying the state of an embedded
	 * database, etc.
	 * @param hierarchyMode the context cache clearing mode to be applied if the
	 * context is part of a hierarchy (may be {@code null})
	 */
	void markApplicationContextDirty(@Nullable HierarchyMode hierarchyMode);

	/**
	 * Update this test context to reflect the state of the currently executing test.
	 * Caution: concurrent invocations of this method might not be thread-safe,
	 * depending on the underlying implementation.
	 * @param testInstance the current test instance (may be {@code null})
	 * @param testMethod the current test method (may be {@code null})
	 * @param testException the exception that was thrown in the test method,
	 * or {@code null} if no exception was thrown
	 */
	void updateState(@Nullable Object testInstance, @Nullable Method testMethod, @Nullable Throwable testException);

}
