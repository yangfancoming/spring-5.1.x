

package org.springframework.aop;

import org.springframework.lang.Nullable;

/**
 * A {@code TargetSource} is used to obtain the current "target" of an AOP invocation, 
 * which will be invoked via reflection if no around advice chooses to end the interceptor chain itself.
 *
 * If a {@code TargetSource} is "static", it will always return the same target,
 * allowing optimizations in the AOP framework. Dynamic target sources can support pooling, hot swapping, etc.
 * Application developers don't usually need to work with {@code TargetSources} directly: this is an AOP framework interface.
 * 该接口代表一个目标对象，在aop调用目标对象的时候，使用该接口返回真实的对象。比如该接口的一个实现：PrototypeTargetSource，那就是每次调用都返回一个全新的对象实例；
 */
public interface TargetSource extends TargetClassAware {

	/**
	 * Return the type of targets returned by this {@link TargetSource}.
	 * Can return {@code null}, although certain usages of a {@code TargetSource}  might just work with a predetermined target class.
	 * @return the type of targets returned by this {@link TargetSource}
	 * 本方法主要用于返回目标bean的Class类型
	 */
	@Override
	@Nullable
	Class<?> getTargetClass();

	/**
	 * Will all calls to {@link #getTarget()} return the same object?
	 * In that case, there will be no need to invoke {@link #releaseTarget(Object)},and the AOP framework can cache the return value of {@link #getTarget()}.
	 * @return {@code true} if the target is immutable
	 * @see #getTarget
	 * 这个方法用户返回当前bean是否为静态的，比如常见的单例bean就是静态的，而prototype就是动态的，
	 * 这里这个方法的主要作用是，对于静态的bean，spring是会对其进行缓存的，在多次使用TargetSource
	 * 获取目标bean对象的时候，其获取的总是同一个对象，通过这种方式提高效率
	 */
	boolean isStatic();

	/**
	 * Return a target instance. Invoked immediately before the AOP framework calls the "target" of an AOP method invocation.
	 * @return the target object which contains the joinpoint,or {@code null} if there is no actual target instance
	 * @throws Exception if the target object can't be resolved
	 * 获取目标bean对象，这里可以根据业务需要进行自行定制
	 */
	@Nullable
	Object getTarget() throws Exception;

	/**
	 * Release the given target object obtained from the {@link #getTarget()} method, if any.
	 * @param target object obtained from a call to {@link #getTarget()}
	 * @throws Exception if the object can't be released
	 * Spring在完目标bean之后会调用这个方法释放目标bean对象，对于一些需要池化的对象，
	 * 这个方法是必须 要实现的，这个方法默认不进行任何处理
	 */
	void releaseTarget(Object target) throws Exception;

}
