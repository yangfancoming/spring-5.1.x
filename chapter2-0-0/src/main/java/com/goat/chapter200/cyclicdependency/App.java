package com.goat.chapter200.cyclicdependency;

import org.junit.Test;

/**
 * 循环依赖 导致堆栈溢出： Exception in thread "main" java.lang.StackOverflowError
 */
public class App {

	@Test
	public void test(){
		System.out.println(new A());
	}

	@Test
	public void test2(){
		System.out.println(new B());
	}
}
