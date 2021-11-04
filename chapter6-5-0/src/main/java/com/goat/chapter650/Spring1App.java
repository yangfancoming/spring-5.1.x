package com.goat.chapter650;

import com.goat.chapter650.dao.BookMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2020/3/25.
 * @ Description:   MapperScannerConfigurer 包扫描 方式一
 * @ author  山羊来了
 * @ date 2020/3/25---10:00
 */
public class Spring1App {

	public ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring1.xml");

	@Test
	public void test() {
		String[] str = context.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	@Test
	public void test2() {
		BookMapper bookMapper = (BookMapper) context.getBean("bookMapper");
		Assert.assertNotNull(bookMapper);
		List<Map> maps = bookMapper.selectById();
		Assert.assertEquals(2,maps.size());
	}
}
