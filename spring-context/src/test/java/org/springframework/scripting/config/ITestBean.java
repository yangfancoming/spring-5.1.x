

package org.springframework.scripting.config;

/**
 * @author Mark Fisher
 */
public interface ITestBean {

	boolean isInitialized();

	boolean isDestroyed();

	ITestBean getOtherBean();

}
