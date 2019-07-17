

package org.springframework.beans.support;

/**

 * @since 29.07.2004
 */
class ProtectedBaseBean {

	private String someProperty;

	public void setSomeProperty(String someProperty) {
		this.someProperty = someProperty;
	}

	public String getSomeProperty() {
		return someProperty;
	}

}
