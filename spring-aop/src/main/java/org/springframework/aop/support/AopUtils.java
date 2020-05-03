

package org.springframework.aop.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.aop.Advisor;
import org.springframework.aop.AopInvocationException;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Utility methods for AOP support code.
 * Mainly for internal use within Spring's AOP support.
 * See {@link org.springframework.aop.framework.AopProxyUtils} for a
 * collection of framework-specific AOP utility methods which depend
 * on internals of Spring's AOP framework implementation.
 * @see org.springframework.aop.framework.AopProxyUtils
 */
public abstract class AopUtils {

	/**
	 * Check whether the given object is a JDK dynamic proxy or a CGLIB proxy.
	 * This method additionally checks if the given object is an instance of {@link SpringProxy}.
	 * @param object the object to check
	 * @see #isJdkDynamicProxy
	 * @see #isCglibProxy
	 */
	public static boolean isAopProxy(@Nullable Object object) {
		/**
		 * 	 1.判断是否是SpringProxy类型，Spring中的所有的代理对象，都会实现这个接口，这个接口是一个标记接口
		 * 	 2.使用JDK的Proxy的isProxyClass方法，判定该对象是否是JDK的代理实现，即是否是基于接口代理；
		 * 	 3.使用ClassUtils的isCglibProxyClass方法判断该对象是否是cglib代理实现；ClassUtils也是Spring中针对反射提供的非常有用的工具类
		 * 	 4.为什么要这样判断?因为Spring需要判定这个对象是否是由spring完成的AOP代理；
		*/
		return (object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) || ClassUtils.isCglibProxyClass(object.getClass())));
	}

	/**
	 * Check whether the given object is a JDK dynamic proxy.
	 * This method goes beyond the implementation of {@link Proxy#isProxyClass(Class)} by additionally checking if the given object is an instance of {@link SpringProxy}.
	 * @param object the object to check
	 * @see java.lang.reflect.Proxy#isProxyClass
	 */
	public static boolean isJdkDynamicProxy(@Nullable Object object) {
		return (object instanceof SpringProxy && Proxy.isProxyClass(object.getClass()));
	}

	/**
	 * Check whether the given object is a CGLIB proxy.
	 * This method goes beyond the implementation of
	 * {@link ClassUtils#isCglibProxy(Object)} by additionally checking if
	 * the given object is an instance of {@link SpringProxy}.
	 * @param object the object to check
	 * @see ClassUtils#isCglibProxy(Object)
	 */
	public static boolean isCglibProxy(@Nullable Object object) {
		return (object instanceof SpringProxy && ClassUtils.isCglibProxy(object));
	}

	/**
	 * 用于获取对象的真实类型
	 * Determine the target class of the given bean instance which might be an AOP proxy.
	 * Returns the target class for an AOP proxy or the plain class otherwise.
	 * @param candidate the instance to check (might be an AOP proxy)
	 * @return the target class (or the plain class of the given object as fallback;never {@code null})
	 * @see org.springframework.aop.TargetClassAware#getTargetClass()
	 * @see org.springframework.aop.framework.AopProxyUtils#ultimateTargetClass(Object)
	 */
	public static Class<?> getTargetClass(Object candidate) {
		Assert.notNull(candidate, "Candidate object must not be null");
		Class<?> result = null;
		if (candidate instanceof TargetClassAware) {
			result = ((TargetClassAware) candidate).getTargetClass();
		}
		if (result == null) {
			result = (isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
		}
		return result;
	}

	/**
	 * Select an invocable method on the target type: either the given method itself
	 * if actually exposed on the target type, or otherwise a corresponding method
	 * on one of the target type's interfaces or on the target type itself.
	 * @param method the method to check
	 * @param targetType the target type to search methods on (typically an AOP proxy)
	 * @return a corresponding invocable method on the target type
	 * @throws IllegalStateException if the given method is not invocable on the given
	 * target type (typically due to a proxy mismatch)
	 * @since 4.3
	 * @see MethodIntrospector#selectInvocableMethod(Method, Class)
	 */
	public static Method selectInvocableMethod(Method method, @Nullable Class<?> targetType) {
		if (targetType == null) {
			return method;
		}
		Method methodToUse = MethodIntrospector.selectInvocableMethod(method, targetType);
		if (Modifier.isPrivate(methodToUse.getModifiers()) && !Modifier.isStatic(methodToUse.getModifiers()) && SpringProxy.class.isAssignableFrom(targetType)) {
			throw new IllegalStateException(String.format(
					"Need to invoke method '%s' found on proxy for target class '%s' but cannot be delegated to target bean. Switch its visibility to package or protected.",
					method.getName(), method.getDeclaringClass().getSimpleName()));
		}
		return methodToUse;
	}

	// Object本身的equals,hashCode,toString,finalize方法，都是需要剔除在代理方法之外的，这四个方法就是来辅助做判断的

	/**
	 * 用于判定给定的方法是否是equals方法
	 * Determine whether the given method is an "equals" method.
	 * @see java.lang.Object#equals
	 */
	public static boolean isEqualsMethod(@Nullable Method method) {
		return ReflectionUtils.isEqualsMethod(method);
	}

	/**
	 * 用于判定给定的方法是否是 hashCode方法
	 * Determine whether the given method is a "hashCode" method.
	 * @see java.lang.Object#hashCode
	 */
	public static boolean isHashCodeMethod(@Nullable Method method) {
		return ReflectionUtils.isHashCodeMethod(method);
	}

	/**
	 * 用于判定给定的方法是否是 toString方法
	 * Determine whether the given method is a "toString" method.
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(@Nullable Method method) {
		return ReflectionUtils.isToStringMethod(method);
	}

	/**
	 * 用于判定给定的方法是否是 finalize方法
	 * Determine whether the given method is a "finalize" method.
	 * @see java.lang.Object#finalize()
	 */
	public static boolean isFinalizeMethod(@Nullable Method method) {
		return (method != null && method.getName().equals("finalize") && method.getParameterCount() == 0);
	}

	/**
	 * 从代理对象上的一个方法，找到真实对象上对应的方法
	 * 举个例子，MyComponent代理之后的对象上的someLogic方法，肯定是属于cglib代理之后的类上的method，
	 * 使用这个method是没法去执行目标MyComponent的someLogic方法，
	 * 这种情况下，就可以使用getMostSpecificMethod，找到真实对象上的someLogic方法，并执行真实方法
	 * Given a method, which may come from an interface, and a target class used in the current AOP invocation,
	 *  find the corresponding target method if there is one.
	 *  E.g. the method may be {@code IFoo.bar()} and the target class may be {@code DefaultFoo}. In this case, the method may be {@code DefaultFoo.bar()}.
	 *  This enables attributes on that method to be found.
	 * <b>NOTE:</b> In contrast to {@link org.springframework.util.ClassUtils#getMostSpecificMethod},
	 * this method resolves Java 5 bridge methods in order to retrieve attributes from the <i>original</i> method definition.
	 * @param method the method to be invoked, which may come from an interface
	 * @param targetClass the target class for the current invocation. May be {@code null} or may not even implement the method.
	 * @return the specific target method, or the original method if the
	 * {@code targetClass} doesn't implement it or is {@code null}
	 * @see org.springframework.util.ClassUtils#getMostSpecificMethod
	 */
	public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
		Class<?> specificTargetClass = (targetClass != null ? ClassUtils.getUserClass(targetClass) : null);
		Method resolvedMethod = ClassUtils.getMostSpecificMethod(method, specificTargetClass);
		// If we are dealing with method with generic parameters, find the original method.
		return BridgeMethodResolver.findBridgedMethod(resolvedMethod);
	}

	/**
	 * 判断一个切入点能否匹配一个指定的类型；
	 * Can the given pointcut apply at all on the given class?
	 * This is an important test as it can be used to optimize out a pointcut for a class.
	 * @param pc the static or dynamic pointcut to check
	 * @param targetClass the class to test
	 * @return whether the pointcut can apply on any method
	 */
	public static boolean canApply(Pointcut pc, Class<?> targetClass) {
		return canApply(pc, targetClass, false);
	}

	/**
	 * 判断一个切入点能否匹配一个指定的类型，是否支持引入匹配
	 * Can the given pointcut apply at all on the given class?
	 * This is an important test as it can be used to optimize out a pointcut for a class.
	 * @param pc the static or dynamic pointcut to check
	 * @param targetClass the class to test
	 * @param hasIntroductions whether or not the advisor chain for this bean includes any introductions
	 * 这个参数是用于指定，判定是否包含introduction，如果不包含introduction，匹配会更加的有效；
	 * @return whether the pointcut can apply on any method
	 */
	public static boolean canApply(Pointcut pc, Class<?> targetClass, boolean hasIntroductions) {
		Assert.notNull(pc, "Pointcut must not be null");
		// 使用 ClassFilter 匹配 class
		if (!pc.getClassFilter().matches(targetClass)) {
			return false;
		}
		MethodMatcher methodMatcher = pc.getMethodMatcher();
		if (methodMatcher == MethodMatcher.TRUE) {
			// No need to iterate the methods if we're matching any method anyway...
			return true;
		}
		IntroductionAwareMethodMatcher introductionAwareMethodMatcher = null;
		if (methodMatcher instanceof IntroductionAwareMethodMatcher) {
			introductionAwareMethodMatcher = (IntroductionAwareMethodMatcher) methodMatcher;
		}
		/*
		 * 查找当前类及其父类（以及父类的父类等等）所实现的接口，由于接口中的方法是 public，
		 * 所以当前类可以继承其父类，和父类的父类中所有的接口方法
		 */
		Set<Class<?>> classes = new LinkedHashSet<>();
		if (!Proxy.isProxyClass(targetClass)) {
			classes.add(ClassUtils.getUserClass(targetClass));
		}
		classes.addAll(ClassUtils.getAllInterfacesForClassAsSet(targetClass));
		for (Class<?> clazz : classes) {
			// 获取当前类的所有方法，包括从父类中继承的方法
			Method[] methods = ReflectionUtils.getAllDeclaredMethods(clazz);
			for (Method method : methods) {
				// 使用 methodMatcher 匹配方法，匹配成功即可立即返回
				if (introductionAwareMethodMatcher != null ? introductionAwareMethodMatcher.matches(method, targetClass, hasIntroductions) : methodMatcher.matches(method, targetClass)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断一个建议(advisor)能否匹配一个指定的类型；
	 * Can the given advisor apply at all on the given class?
	 * This is an important test as it can be used to optimize  out a advisor for a class.
	 * @param advisor the advisor to check
	 * @param targetClass class we're testing
	 * @return whether the pointcut can apply on any method
	 */
	public static boolean canApply(Advisor advisor, Class<?> targetClass) {
		return canApply(advisor, targetClass, false);
	}

	/**
	 * 判断一个建议(advisor)能否匹配一个指定的类型，是否支持引入匹配；
	 * Can the given advisor apply at all on the given class?
	 * This is an important test as it can be used to optimize out a advisor for a class.
	 * This version also takes into account introductions (for IntroductionAwareMethodMatchers).
	 * @param advisor the advisor to check
	 * @param targetClass class we're testing
	 * @param hasIntroductions whether or not the advisor chain for this bean includes any introductions
	 * @return whether the pointcut can apply on any method
	 */
	public static boolean canApply(Advisor advisor, Class<?> targetClass, boolean hasIntroductions) {
		if (advisor instanceof IntroductionAdvisor) {
			/*
			 * 从通知器中获取类型过滤器 ClassFilter，并调用 matchers 方法进行匹配。
			 * ClassFilter 接口的实现类 AspectJExpressionPointcut 为例，该类的
			 * 匹配工作由 AspectJ 表达式解析器负责，具体匹配细节这个就没法分析了，我
			 * AspectJ 表达式的工作流程不是很熟
			 */
			return ((IntroductionAdvisor) advisor).getClassFilter().matches(targetClass);
		}else if (advisor instanceof PointcutAdvisor) {
			PointcutAdvisor pca = (PointcutAdvisor) advisor;
			// 对于普通类型的通知器，这里继续调用重载方法进行筛选
			return canApply(pca.getPointcut(), targetClass, hasIntroductions);
		}else {
			// It doesn't have a pointcut so we assume it applies.
			return true;
		}
	}

	/**
	 * 在一组建议(advisor)中，返回能够匹配指定类型的建议者列表；
	 * Determine the sublist of the {@code candidateAdvisors} list that is applicable to the given class.
	 * @param candidateAdvisors the Advisors to evaluate
	 * @param clazz the target class
	 * @return sublist of Advisors that can apply to an object of the given class (may be the incoming List as-is)
	 */
	public static List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> clazz) {
		if (candidateAdvisors.isEmpty()) {
			return candidateAdvisors;
		}
		List<Advisor> eligibleAdvisors = new ArrayList<>();
		for (Advisor candidate : candidateAdvisors) {
			// 筛选 IntroductionAdvisor 类型的通知器
			if (candidate instanceof IntroductionAdvisor && canApply(candidate, clazz)) {
				eligibleAdvisors.add(candidate);
			}
		}
		boolean hasIntroductions = !eligibleAdvisors.isEmpty();
		for (Advisor candidate : candidateAdvisors) {
			if (candidate instanceof IntroductionAdvisor) {
				// already processed
				continue;
			}
			// 筛选普通类型的通知器
			if (canApply(candidate, clazz, hasIntroductions)) {
				eligibleAdvisors.add(candidate);
			}
		}
		return eligibleAdvisors;
	}

	/**
	 * 执行一个目标方法；这个方法其实就是method.invoke方法的更完善的方法，指在target对象上，使用args参数列表执行method
	 * Invoke the given target via reflection, as part of an AOP method invocation.
	 * @param target the target object
	 * @param method the method to invoke
	 * @param args the arguments for the method
	 * @return the invocation result, if any
	 * @throws Throwable if thrown by the target method
	 * @throws org.springframework.aop.AopInvocationException in case of a reflection error
	 */
	@Nullable
	public static Object invokeJoinpointUsingReflection(@Nullable Object target, Method method, Object[] args) throws Throwable {
		// Use reflection to invoke the method.
		try {
			ReflectionUtils.makeAccessible(method);
			// 直接使用反射执行目标方法
			return method.invoke(target, args);
		}catch (InvocationTargetException ex) {
			// 所有执行过程中的异常都需要包装之后抛回给调用者.
			// Invoked method threw a checked exception.We must rethrow it. The client won't see the interceptor.
			throw ex.getTargetException();
		}catch (IllegalArgumentException ex) {
			throw new AopInvocationException("AOP configuration seems to be invalid: tried calling method [" + method + "] on target [" + target + "]", ex);
		}catch (IllegalAccessException ex) {
			throw new AopInvocationException("Could not access method [" + method + "]", ex);
		}
	}

}
