package com.goat.chapter800.item01;

import org.junit.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.mockito.Mockito.*;


/**
 * Created by Administrator on 2019/11/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/11/27---20:12
 */
public class App2 {

	/**
	 * 验证执行执行顺序
	 * 验证执行顺序是非常灵活的-你不需要一个一个的验证所有交互,只需要验证你感兴趣的对象即可。 另外，你可以仅通过那些需要验证顺序的mock对象来创建InOrder对象。
	*/
	@Test
	public void test6(){
		// A. Single mock whose methods must be invoked in a particular order
		// A. 验证mock一个对象的函数执行顺序
		List singleMock = mock(List.class);

		//using a single mock
		singleMock.add("was added first");
		singleMock.add("was added second");

		//create an inOrder verifier for a single mock
		// 为该mock对象创建一个inOrder对象
		InOrder inOrder = inOrder(singleMock);

		//following will make sure that add is first called with "was added first, then with "was added second"
		// 确保add函数首先执行的是add("was added first"),然后才是add("was added second")
		inOrder.verify(singleMock).add("was added first");
		inOrder.verify(singleMock).add("was added second");

		// B. Multiple mocks that must be used in a particular order
		// B .验证多个mock对象的函数执行顺序
		List firstMock = mock(List.class);
		List secondMock = mock(List.class);

		//using mocks
		firstMock.add("was called first");
		secondMock.add("was called second");

		//create inOrder object passing any mocks that need to be verified in order
		// 为这两个Mock对象创建inOrder对象
		InOrder inOrder2 = inOrder(firstMock, secondMock);

		//following will make sure that firstMock was called before secondMock
		// 验证它们的执行顺序
		inOrder2.verify(firstMock).add("was called first");
		inOrder2.verify(secondMock).add("was called second");
		// Oh, and A + B can be mixed together at will
	}


	/**
	 * 确保交互(interaction)操作不会执行在mock对象上
	*/
	@Test
	public void test7(){
		List mockOne = mock(List.class);
		//using mocks - only mockOne is interacted
		// 使用Mock对象
		mockOne.add("one");

		//ordinary verification
		// 普通验证
		verify(mockOne).add("one");

		//verify that method was never called on a mock
		// 验证某个交互是否从未被执行
		verify(mockOne, never()).add("two");

		//verify that other mocks were not interacted
		// 验证mock对象没有交互过
//		verifyZeroInteractions(mockTwo, mockThree);
	}


	/**
	 * 为连续的调用做测试桩 (stub)
	 */
	@Test
	public void test10(){
		MyMock mock = mock(MyMock.class);
		// 简写方式 第一次调用时返回"one",第二次返回"two",第三次返回"three"
//		when(mock.someMethod("some arg"))
//				.thenReturn("one", "two", "three");


		when(mock.someMethod("some arg"))
				.thenThrow(new RuntimeException())
				.thenReturn("foo");

		//First call: throws runtime exception:
		// 第一次调用 : 抛出运行时异常
		System.out.println(mock.someMethod("some arg"));

		//Second call: prints "foo"
		// 第二次调用 : 输出"foo"
		System.out.println(mock.someMethod("some arg"));

		//Any consecutive call: prints "foo" as well (last stubbing wins).
		// 后续调用 : 也是输出"foo"
		System.out.println(mock.someMethod("some arg"));


	}
}
