

package org.springframework.expression.spel.support;

import org.springframework.expression.TypedValue;

/**
 * A {@link TypedValue} for booleans.
 *
 * @author Andy Clement
 * @since 3.0
 */
public final class BooleanTypedValue extends TypedValue {

	/**
	 * True.
	 */
	public static final BooleanTypedValue TRUE = new BooleanTypedValue(true);

	/**
	 * False.
	 */
	public static final BooleanTypedValue FALSE = new BooleanTypedValue(false);


	private BooleanTypedValue(boolean b) {
		super(b);
	}


	public static BooleanTypedValue forValue(boolean b) {
		return (b ? TRUE : FALSE);
	}

}
