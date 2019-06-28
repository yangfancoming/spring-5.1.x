

package org.springframework.context.annotation;

/**
 * @author Juergen Hoeller
 */
@Configuration
class MyTestBean {

	@Bean
	public org.springframework.tests.sample.beans.TestBean myTestBean() {
		return new org.springframework.tests.sample.beans.TestBean();
	}

}
