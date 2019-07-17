

package org.springframework.tests.sample.beans;

import java.io.Serializable;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;

/**

 * @since 21.08.2003
 */
@SuppressWarnings("serial")
public class DerivedTestBean extends TestBean implements Serializable, BeanNameAware, DisposableBean {

	private String beanName;

	private boolean initialized;

	private boolean destroyed;


	public DerivedTestBean() {
	}

	public DerivedTestBean(String[] names) {
		if (names == null || names.length < 2) {
			throw new IllegalArgumentException("Invalid names array");
		}
		setName(names[0]);
		setBeanName(names[1]);
	}

	public static DerivedTestBean create(String[] names) {
		return new DerivedTestBean(names);
	}


	@Override
	public void setBeanName(String beanName) {
		if (this.beanName == null || beanName == null) {
			this.beanName = beanName;
		}
	}

	@Override
	public String getBeanName() {
		return beanName;
	}

	public void setActualSpouse(TestBean spouse) {
		setSpouse(spouse);
	}

	public void setSpouseRef(String name) {
		setSpouse(new TestBean(name));
	}

	@Override
	public TestBean getSpouse() {
		return (TestBean) super.getSpouse();
	}


	public void initialize() {
		this.initialized = true;
	}

	public boolean wasInitialized() {
		return initialized;
	}


	@Override
	public void destroy() {
		this.destroyed = true;
	}

	@Override
	public boolean wasDestroyed() {
		return destroyed;
	}

}
