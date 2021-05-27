package com.goat.chapter800.item01;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import static org.mockito.Mockito.*;
/**
 * Created by Administrator on 2021/5/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/5/27---17:12
 */
public class App1 {

	// mock creation
	List mockedList = mock(List.class);

	/**
	 * 验证某些行为
	 * 一旦mock对象被创建了，mock对象会记住所有的交互。然后你就可能选择性的验证你感兴趣的交互。
	*/
	@Test
	public void test1() {
		//using mock object
		mockedList.add("one");
		mockedList.clear();
		// verification 验证
		verify(mockedList).add("one");
		verify(mockedList).clear();
	}

	/**
	 * 如何做一些测试桩 (Stub)
	*/
	@Test
	public void test2() {
		//You can mock concrete classes, not only interfaces
		LinkedList mockedList = mock(LinkedList.class);
		//stubbing
		when(mockedList.get(0)).thenReturn("first");
		when(mockedList.get(1)).thenThrow(new RuntimeException());
		//following prints "first"
		System.out.println(mockedList.get(0));
		//following throws runtime exception
		System.out.println(mockedList.get(1));
		//following prints "null" because get(999) was not stubbed
		System.out.println(mockedList.get(999));
		//Although it is possible to verify a stubbed invocation, usually it's just redundant
		//If your code cares what get(0) returns then something else breaks (often before even verify() gets executed).
		//If your code doesn't care what get(0) returns then it should not be stubbed. Not convinced? See here.
		// 验证get(0)被调用的次数
		verify(mockedList).get(0);
	}


	/**
	 * 参数匹配器 (matchers)
	*/
	@Test
	public void test3() {
		LinkedList mockedList = mock(LinkedList.class);
		//stubbing using built-in anyInt() argument matcher
		// 使用内置的anyInt()参数匹配器
		when(mockedList.get(anyInt())).thenReturn("element");
		//following prints "element"
		// 输出element
		System.out.println(mockedList.get(999));
		//you can also verify using an argument matcher
		// 你也可以验证参数匹配器
		verify(mockedList).get(anyInt());
	}


	/**
	 * 验证函数的确切、最少、从未调用次数
	 * verify函数默认验证的是执行了times(1)，也就是某个测试函数是否执行了1次.因此，times(1)通常被省略了。
	*/
	@Test
	public void test4() {
		//using mock
		mockedList.add("once");

		mockedList.add("twice");
		mockedList.add("twice");

		mockedList.add("three times");
		mockedList.add("three times");
		mockedList.add("three times");

		//following two verifications work exactly the same - times(1) is used by default
		// 下面的两个验证函数效果一样,因为verify默认验证的就是times(1)
		verify(mockedList).add("once");
		verify(mockedList, times(1)).add("once");

		//exact number of invocations verification
		// 验证具体的执行次数
		verify(mockedList, times(2)).add("twice");
		verify(mockedList, times(3)).add("three times");

		//verification using never(). never() is an alias to times(0)
		// 使用never()进行验证,never相当于times(0)
		verify(mockedList, never()).add("never happened");

		//verification using atLeast()/atMost()
		// 使用atLeast()/atMost()
		verify(mockedList, atLeastOnce()).add("three times");
		verify(mockedList, atLeast(2)).add("five times");
		verify(mockedList, atMost(5)).add("three times");
	}

	/**
	 * 为返回值为void的函数通过Stub抛出异常
	 * 最初，stubVoid(Object) 函数用于为无返回值的函数打桩。现在stubVoid()函数已经过时,doThrow(Throwable)成为了它的继承者。
	 * 这是为了提升与 doAnswer(Answer) 函数族的可读性与一致性。
	*/
	@Test
	public void test5() {
		doThrow(new RuntimeException()).when(mockedList).clear();
		//following throws RuntimeException:
		// 调用这句代码会抛出异常
		mockedList.clear();
	}
}





