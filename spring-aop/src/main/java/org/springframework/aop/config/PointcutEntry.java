

package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

/**
 * {@link ParseState} entry representing a pointcut.
 * @since 2.0
 */
public class PointcutEntry implements ParseState.Entry {

	private final String name;

	/**
	 * Creates a new instance of the {@link PointcutEntry} class.
	 * @param name the bean name of the pointcut
	 */
	public PointcutEntry(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Pointcut '" + this.name + "'";
	}

}
