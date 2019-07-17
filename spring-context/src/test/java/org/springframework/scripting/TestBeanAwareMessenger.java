

package org.springframework.scripting;

import org.springframework.tests.sample.beans.TestBean;


public interface TestBeanAwareMessenger extends ConfigurableMessenger {

	TestBean getTestBean();

	void setTestBean(TestBean testBean);

}
