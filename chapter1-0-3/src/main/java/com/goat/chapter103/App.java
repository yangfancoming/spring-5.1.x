package com.goat.chapter103;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by 64274 on 2019/8/18.
 * @ Description: Spring资源文件读取
 * @ author  山羊来了
 * @ date 2019/8/18---12:33
 */
public class App {

	// 从资源文件夹下加载
	@Test
	public void test1() {
		Resource resource = new ClassPathResource("application.xml");
		print(resource);
	}

	@Test
	public void test2() {
		// 使用类信息加载   doit 为啥读取不到配置文件？？
		Resource resource = new ClassPathResource("application.xml", Person.class);
		print(resource);
	}

	@Test
	public void test3() {
		// 使用类加载器从资源文件夹下加载
		Resource resource = new ClassPathResource("application.xml", Person.class.getClassLoader());
		print(resource);
	}

	@Test
	public void test4() {
		// 使用DefaultResourceLoader加载
		Resource resource = new DefaultResourceLoader().getResource("application.xml");
		print(resource);
	}

	// 打印资源文件内容
	public void print(Resource resource) {
		byte[] read = new byte[1000];
		try {
			resource.getInputStream().read(read, 0, read.length);
			System.out.println(new String(read));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
