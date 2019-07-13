

package org.springframework.jdbc.core.test;

import java.math.BigDecimal;

/**
 * @author Thomas Risberg
 */
public class Person {

	private String name;

	private long age;

	private java.util.Date birth_date;

	private BigDecimal balance;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public java.util.Date getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(java.util.Date birth_date) {
		this.birth_date = birth_date;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

}
