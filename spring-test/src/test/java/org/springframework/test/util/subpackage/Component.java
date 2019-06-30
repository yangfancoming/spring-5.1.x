

package org.springframework.test.util.subpackage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Simple POJO representing a <em>component</em>; intended for use in
 * unit tests.
 *
 * @author Sam Brannen
 * @since 3.1
 */
public class Component {

	private Integer number;
	private String text;


	public Integer getNumber() {
		return this.number;
	}

	public String getText() {
		return this.text;
	}

	@Autowired
	protected void configure(Integer number, String text) {
		this.number = number;
		this.text = text;
	}

	@PostConstruct
	protected void init() {
		Assert.state(number != null, "number must not be null");
		Assert.state(StringUtils.hasText(text), "text must not be empty");
	}

	@PreDestroy
	protected void destroy() {
		this.number = null;
		this.text = null;
	}

	int subtract(int a, int b) {
		return a - b;
	}

	int add(int... args) {
		int sum = 0;
		for (int i = 0; i < args.length; i++) {
			sum += args[i];
		}
		return sum;
	}

	int multiply(Integer... args) {
		int product = 1;
		for (int i = 0; i < args.length; i++) {
			product *= args[i];
		}
		return product;
	}

}
