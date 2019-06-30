

package org.springframework.test.util.subpackage;

/**
 * Simple class with static fields; intended for use in unit tests.
 *
 * @author Sam Brannen
 * @since 4.2
 */
public class StaticFields {

	public static String publicField = "public";

	private static String privateField = "private";


	public static void reset() {
		publicField = "public";
		privateField = "private";
	}

	public static String getPrivateField() {
		return privateField;
	}

}
