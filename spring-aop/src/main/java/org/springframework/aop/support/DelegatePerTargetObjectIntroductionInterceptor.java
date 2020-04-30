

package org.springframework.aop.support;

import java.util.Map;
import java.util.WeakHashMap;

import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.DynamicIntroductionAdvice;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * Convenient implementation of the {@link org.springframework.aop.IntroductionInterceptor} interface.
 *
 * This differs from {@link DelegatingIntroductionInterceptor} in that a single
 * instance of this class can be used to advise multiple target objects, and each target
 * object will have its <i>own</i> delegate (whereas DelegatingIntroductionInterceptor
 * shares the same delegate, and hence the same state across all targets).
 *
 * The {@code suppressInterface} method can be used to suppress interfaces
 * implemented by the delegate class but which should not be introduced to the owning AOP proxy.
 *
 * An instance of this class is serializable if the delegates are.
 *
 * <i>Note: There are some implementation similarities between this class and
 * {@link DelegatingIntroductionInterceptor} that suggest a possible refactoring
 * to extract a common ancestor class in the future.</i>
 * @since 2.0
 * @see #suppressInterface
 * @see DelegatingIntroductionInterceptor
 */
@SuppressWarnings("serial")
public class DelegatePerTargetObjectIntroductionInterceptor extends IntroductionInfoSupport
		implements IntroductionInterceptor {

	/**
	 * Hold weak references to keys as we don't want to interfere with garbage collection..
	 */
	private final Map<Object, Object> delegateMap = new WeakHashMap<>();

	private Class<?> defaultImplType;

	private Class<?> interfaceType;


	public DelegatePerTargetObjectIntroductionInterceptor(Class<?> defaultImplType, Class<?> interfaceType) {
		this.defaultImplType = defaultImplType;
		this.interfaceType = interfaceType;
		// Create a new delegate now (but don't store it in the map).
		// We do this for two reasons:
		// 1) to fail early if there is a problem instantiating delegates
		// 2) to populate the interface map once and once only
		Object delegate = createNewDelegate();
		implementInterfacesOnObject(delegate);
		suppressInterface(IntroductionInterceptor.class);
		suppressInterface(DynamicIntroductionAdvice.class);
	}


	/**
	 * Subclasses may need to override this if they want to perform custom
	 * behaviour in around advice. However, subclasses should invoke this
	 * method, which handles introduced interfaces and forwarding to the target.
	 */
	@Override
	@Nullable
	public Object invoke(MethodInvocation mi) throws Throwable {
		if (isMethodOnIntroducedInterface(mi)) {
			Object delegate = getIntroductionDelegateFor(mi.getThis());
			// Using the following method rather than direct reflection,
			// we get correct handling of InvocationTargetException
			// if the introduced method throws an exception.
			Object retVal = AopUtils.invokeJoinpointUsingReflection(delegate, mi.getMethod(), mi.getArguments());

			// Massage return value if possible: if the delegate returned itself,
			// we really want to return the proxy.
			if (retVal == delegate && mi instanceof ProxyMethodInvocation) {
				retVal = ((ProxyMethodInvocation) mi).getProxy();
			}
			return retVal;
		}

		return doProceed(mi);
	}

	/**
	 * Proceed with the supplied {@link org.aopalliance.intercept.MethodInterceptor}.
	 * Subclasses can override this method to intercept method invocations on the
	 * target object which is useful when an introduction needs to monitor the object
	 * that it is introduced into. This method is <strong>never</strong> called for
	 * {@link MethodInvocation MethodInvocations} on the introduced interfaces.
	 */
	protected Object doProceed(MethodInvocation mi) throws Throwable {
		// If we get here, just pass the invocation on.
		return mi.proceed();
	}

	private Object getIntroductionDelegateFor(Object targetObject) {
		synchronized (this.delegateMap) {
			if (this.delegateMap.containsKey(targetObject)) {
				return this.delegateMap.get(targetObject);
			}else {
				Object delegate = createNewDelegate();
				this.delegateMap.put(targetObject, delegate);
				return delegate;
			}
		}
	}

	private Object createNewDelegate() {
		try {
			return ReflectionUtils.accessibleConstructor(this.defaultImplType).newInstance();
		}catch (Throwable ex) {
			throw new IllegalArgumentException("Cannot create default implementation for '" + this.interfaceType.getName() + "' mixin (" + this.defaultImplType.getName() + "): " + ex);
		}
	}

}
