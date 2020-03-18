package com.goat.chapter105.item07.common;

import org.springframework.beans.factory.annotation.Autowired;


public class TestService4 {

	@Autowired(required = false) TestDao testDao;
//	@Autowired TestDao testDao;

	public void printDao(){
		System.out.println(testDao);
	}
}
