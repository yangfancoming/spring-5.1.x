package com.goat.chapter800.item01;

import org.junit.Test;

import java.util.List;
import static org.mockito.Mockito.*;
/**
 * Created by Administrator on 2021/5/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/5/27---17:12
 */
public class App1 {

	/**
	 * 验证某些行为
	 * 一旦mock对象被创建了，mock对象会记住所有的交互。然后你就可能选择性的验证你感兴趣的交互。
	*/
	@Test
	public void test1() {
		// mock creation
		List mockedList = mock(List.class);
		//using mock object
		mockedList.add("one");
		mockedList.clear();
		// verification 验证
		verify(mockedList).add("one");
		verify(mockedList).clear();
	}

}





