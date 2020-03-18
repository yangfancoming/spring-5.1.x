package com.goat.chapter105.item07.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TestService {

	@Autowired TestDao testDao;

	public void printDao(){
		System.out.println(testDao);
	}
}
