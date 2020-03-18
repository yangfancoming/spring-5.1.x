package com.goat.chapter105.item07.common;

import org.springframework.stereotype.Repository;


@Repository
public class TestDao {

	private String mark = "1";

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	@Override
	public String toString() {
		return "TestDao{" + "mark='" + mark + '\'' + '}';
	}
}
