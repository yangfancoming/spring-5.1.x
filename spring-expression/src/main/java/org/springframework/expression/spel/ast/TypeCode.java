

package org.springframework.expression.spel.ast;

/**
 * Captures primitive types and their corresponding class objects, plus one special entry
 * that represents all reference (non-primitive) types.
 *
 * @author Andy Clement
 */
public enum TypeCode {

	/**
	 * An {@link Object}.
	 */
	OBJECT(Object.class),

	/**
	 * A {@code boolean}.
	 */
	BOOLEAN(Boolean.TYPE),

	/**
	 * A {@code byte}.
	 */
	BYTE(Byte.TYPE),

	/**
	 * A {@code char}.
	 */
	CHAR(Character.TYPE),

	/**
	 * A {@code double}.
	 */
	DOUBLE(Double.TYPE),

	/**
	 * A {@code float}.
	 */
	FLOAT(Float.TYPE),

	/**
	 * An {@code int}.
	 */
	INT(Integer.TYPE),

	/**
	 * A {@code long}.
	 */
	LONG(Long.TYPE),

	/**
	 * An {@link Object}.
	 */
	SHORT(Short.TYPE);


	private Class<?> type;


	TypeCode(Class<?> type) {
		this.type = type;
	}


	public Class<?> getType() {
		return this.type;
	}


	public static TypeCode forName(String name) {
		TypeCode[] tcs = values();
		for (int i = 1; i < tcs.length; i++) {
			if (tcs[i].name().equalsIgnoreCase(name)) {
				return tcs[i];
			}
		}
		return OBJECT;
	}

	public static TypeCode forClass(Class<?> clazz) {
		TypeCode[] allValues = TypeCode.values();
		for (TypeCode typeCode : allValues) {
			if (clazz == typeCode.getType()) {
				return typeCode;
			}
		}
		return OBJECT;
	}

}
