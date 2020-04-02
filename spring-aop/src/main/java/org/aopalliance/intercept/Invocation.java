

package org.aopalliance.intercept;

/**
 * This interface represents an invocation in the program.
 * 此接口表示程序中的调用。
 * An invocation is a joinpoint and can be intercepted by an interceptor.
 * 调用是一个连接点，可以被拦截器拦截
 */
public interface Invocation extends Joinpoint {

	/**
	 * Get the arguments as an array object.
	 * It is possible to change element values within this
	 * array to change the arguments.
	 * @return the argument of the invocation
	 */
	Object[] getArguments();

}
