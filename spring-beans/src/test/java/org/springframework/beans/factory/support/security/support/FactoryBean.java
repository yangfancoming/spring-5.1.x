
package org.springframework.beans.factory.support.security.support;


public class FactoryBean {

	public static Object makeStaticInstance() {
		System.getProperties();
		return new Object();
	}

	protected static Object protectedStaticInstance() {
		return "protectedStaticInstance";
	}

	public Object makeInstance() {
		System.getProperties();
		return new Object();
	}
}
