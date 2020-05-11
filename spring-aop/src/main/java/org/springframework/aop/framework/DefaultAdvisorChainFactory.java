

package org.springframework.aop.framework;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aopalliance.intercept.Interceptor;
import org.aopalliance.intercept.MethodInterceptor;

import org.springframework.aop.Advisor;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionAwareMethodMatcher;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.lang.Nullable;

/**
 * A simple but definitive way of working out an advice chain for a Method,
 * given an {@link Advised} object. Always rebuilds each advice chain;
 * caching can be provided by subclasses.
 * @since 2.0.3
 */
@SuppressWarnings("serial")
public class DefaultAdvisorChainFactory implements AdvisorChainFactory, Serializable {

	@Override
	public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Advised config, Method method, @Nullable Class<?> targetClass) {

		// This is somewhat tricky... We have to process introductions first,
		// but we need to preserve order in the ultimate list.
		// registry 为 DefaultAdvisorAdapterRegistry 类型
		AdvisorAdapterRegistry registry = GlobalAdvisorAdapterRegistry.getInstance();
		Advisor[] advisors = config.getAdvisors();
		List<Object> interceptorList = new ArrayList<>(advisors.length);
		Class<?> actualClass = (targetClass != null ? targetClass : method.getDeclaringClass());
		Boolean hasIntroductions = null;
		// 遍历通知器列表
		for (Advisor advisor : advisors) {
			if (advisor instanceof PointcutAdvisor) {
				// Add it conditionally.
				PointcutAdvisor pointcutAdvisor = (PointcutAdvisor) advisor;
				// 这里判断切面逻辑的调用链是否提前进行过过滤，如果进行过，则不再进行目标方法的匹配，
				// 如果没有，则再进行一次匹配。这里我们使用的AnnotationAwareAspectJAutoProxyCreator
				// 在生成切面逻辑的时候就已经进行了过滤，因而这里返回的是true，本文最开始也对这里进行了讲解
				if (config.isPreFiltered() || pointcutAdvisor.getPointcut().getClassFilter().matches(actualClass)) {
					MethodMatcher mm = pointcutAdvisor.getPointcut().getMethodMatcher();
					boolean match;
					// 这里进行匹配的时候，首先会检查是否为IntroductionAwareMethodMatcher类型的
					// Matcher，如果是，则调用其定义的matches()方法进行匹配，如果不是，则直接调用
					// 当前切面的matches()方法进行匹配。这里由于前面进行匹配时可能存在部分在静态匹配时
					// 无法确认的方法匹配结果，因而这里调用是必要的，而对于能够确认的匹配逻辑，这里调用
					// 也是非常迅速的，因为前面已经对匹配结果进行了缓存
					if (mm instanceof IntroductionAwareMethodMatcher) {
						if (hasIntroductions == null) {
							// 判断切面逻辑中是否有IntroductionAdvisor类型的Advisor
							hasIntroductions = hasMatchingIntroductions(advisors, actualClass);
						}
						match = ((IntroductionAwareMethodMatcher) mm).matches(method, actualClass, hasIntroductions);
					}else {
						match = mm.matches(method, actualClass);
					}
					if (match) {
						// 将Advisor对象转换为MethodInterceptor数组
						// 将 advisor 中的 advice 转成相应的拦截器
						MethodInterceptor[] interceptors = registry.getInterceptors(advisor);
						// 判断如果是动态匹配，则使用InterceptorAndDynamicMethodMatcher对其进行封装
						// 若 isRuntime 返回 true，则表明 MethodMatcher 要在运行时做一些检测
						if (mm.isRuntime()) {
							// Creating a new object instance in the getInterceptors() method
							// isn't a problem as we normally cache created chains.
							for (MethodInterceptor interceptor : interceptors) {
								interceptorList.add(new InterceptorAndDynamicMethodMatcher(interceptor, mm));
							}
						}else {
							// 如果是静态匹配，则直接将调用链返回
							interceptorList.addAll(Arrays.asList(interceptors));
						}
					}
				}
			}else if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				// 判断如果为IntroductionAdvisor类型的Advisor，则将调用链封装为Interceptor数组
				// IntroductionAdvisor 类型的通知器，仅需进行类级别的匹配即可
				if (config.isPreFiltered() || ia.getClassFilter().matches(actualClass)) {
					Interceptor[] interceptors = registry.getInterceptors(advisor);
					interceptorList.addAll(Arrays.asList(interceptors));
				}
			}else {
				// 这里是提供的使用自定义的转换器对Advisor进行转换的逻辑，因为getInterceptors()方法中
				// 会使用相应的Adapter对目标Advisor进行匹配，如果能匹配上，通过其getInterceptor()方法
				// 将自定义的Advice转换为MethodInterceptor对象
				Interceptor[] interceptors = registry.getInterceptors(advisor);
				interceptorList.addAll(Arrays.asList(interceptors));
			}
		}
		return interceptorList;
	}

	/**
	 * Determine whether the Advisors contain matching introductions.
	 */
	private static boolean hasMatchingIntroductions(Advisor[] advisors, Class<?> actualClass) {
		for (Advisor advisor : advisors) {
			if (advisor instanceof IntroductionAdvisor) {
				IntroductionAdvisor ia = (IntroductionAdvisor) advisor;
				if (ia.getClassFilter().matches(actualClass)) {
					return true;
				}
			}
		}
		return false;
	}
}
