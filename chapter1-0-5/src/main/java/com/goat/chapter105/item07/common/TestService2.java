package com.goat.chapter105.item07.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TestService2 {

	@Autowired TestDao testDao2;

	public void printDao(){
		System.out.println(testDao2);
	}
}
