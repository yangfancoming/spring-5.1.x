

package org.springframework.context.annotation;

import example.scannable.FooService;

import org.springframework.core.convert.converter.Converter;

/**
 * @author Juergen Hoeller
 */
public class FooServiceDependentConverter implements Converter<String, org.springframework.tests.sample.beans.TestBean> {

	@SuppressWarnings("unused")
	private FooService fooService;

	public void setFooService(FooService fooService) {
		this.fooService = fooService;
	}

	@Override
	public org.springframework.tests.sample.beans.TestBean convert(String source) {
		return new org.springframework.tests.sample.beans.TestBean(source);
	}

}
