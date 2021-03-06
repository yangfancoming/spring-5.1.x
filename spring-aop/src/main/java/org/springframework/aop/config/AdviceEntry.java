

package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

/**
 * {@link ParseState} entry representing an advice element.
 * @since 2.0
 */
public class AdviceEntry implements ParseState.Entry {

	private final String kind;

	/**
	 * Creates a new instance of the {@link AdviceEntry} class.
	 * @param kind the kind of advice represented by this entry (before, after, around, etc.)
	 */
	public AdviceEntry(String kind) {
		this.kind = kind;
	}

	@Override
	public String toString() {
		return "Advice (" + this.kind + ")";
	}

}
