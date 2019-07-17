

package org.springframework.aop.framework;

/**
 * Listener to be registered on {@link ProxyCreatorSupport} objects
 * Allows for receiving callbacks on activation and change of advice.
 *
 * @author Rod Johnson

 * @see ProxyCreatorSupport#addListener
 */
public interface AdvisedSupportListener {

	/**
	 * Invoked when the first proxy is created.
	 * @param advised the AdvisedSupport object
	 */
	void activated(AdvisedSupport advised);

	/**
	 * Invoked when advice is changed after a proxy is created.
	 * @param advised the AdvisedSupport object
	 */
	void adviceChanged(AdvisedSupport advised);

}
