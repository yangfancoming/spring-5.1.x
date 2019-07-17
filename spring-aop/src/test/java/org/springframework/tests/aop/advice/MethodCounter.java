

package org.springframework.tests.aop.advice;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Abstract superclass for counting advices etc.
 *
 * @author Rod Johnson

 */
@SuppressWarnings("serial")
public class MethodCounter implements Serializable {

	/** Method name --> count, does not understand overloading */
	private HashMap<String, Integer> map = new HashMap<>();

	private int allCount;

	protected void count(Method m) {
		count(m.getName());
	}

	protected void count(String methodName) {
		Integer i = map.get(methodName);
		i = (i != null) ? new Integer(i.intValue() + 1) : new Integer(1);
		map.put(methodName, i);
		++allCount;
	}

	public int getCalls(String methodName) {
		Integer i = map.get(methodName);
		return (i != null ? i.intValue() : 0);
	}

	public int getCalls() {
		return allCount;
	}

	/**
	 * A bit simplistic: just wants the same class.
	 * Doesn't worry about counts.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return (other != null && other.getClass() == this.getClass());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
