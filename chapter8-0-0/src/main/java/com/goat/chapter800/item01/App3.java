package com.goat.chapter800.item01;

import org.junit.Test;


import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Created by Administrator on 2019/11/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/27---20:12
 */
public class App3 {

	/**
	 * 你可以为真实对象创建一个监控(spy)对象。当你使用这个spy对象时真实的对象也会也调用，除非它的函数被stub了。
	 * 尽量少使用spy对象，使用时也需要小心形式，例如spy对象可以用来处理遗留代码。
	 * 监控一个真实的对象可以与“局部mock对象”概念结合起来。在1.8之前，mockito的监控功能并不是真正的局部mock对象。
	 * 原因是我们认为局部mock对象的实现方式并不好，在某些时候我发现一些使用局部mock对象的合法用例。
	*/
	@Test
	public void test13(){
		List list = new LinkedList();
		List spy = spy(list);

		//optionally, you can stub out some methods:
		// 你可以为某些函数打桩
//		when(spy.size()).thenReturn(100);

		//using the spy calls *real* methods
		// 通过spy对象调用真实对象的函数
		spy.add("one");
		spy.add("two");

		//prints "one" - the first element of a list
		// 输出第一个元素
		System.out.println(spy.get(0));

		//size() method was stubbed - 100 is printed
		// 因为size()函数被打桩了,因此这里返回的是100 。 注释掉打桩代码  则这里返回 2
		System.out.println(spy.size());

		//optionally, you can verify
		// 交互验证
		verify(spy).add("one");
		verify(spy).add("two");
	}


	/**
	 *
	*/
	@Test
	public void test7(){

	}


	/**
	 *
	 */
	@Test
	public void test10(){

	}
}
