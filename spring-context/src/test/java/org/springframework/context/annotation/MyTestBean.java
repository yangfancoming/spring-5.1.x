

package org.springframework.context.annotation;


@Configuration
class MyTestBean {

	@Bean
	public org.springframework.tests.sample.beans.TestBean myTestBean() {
		return new org.springframework.tests.sample.beans.TestBean();
	}

}
