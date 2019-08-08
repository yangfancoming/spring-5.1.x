

package org.springframework.scripting.config;


public interface ITestBean {

	boolean isInitialized();

	boolean isDestroyed();

	ITestBean getOtherBean();

}
