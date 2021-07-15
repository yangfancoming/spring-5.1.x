package com.goat.chapter201.Import;


import org.springframework.context.annotation.Import;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---17:08
 */
@Import({MyImportBeanDefinitionRegistrar.class})
public class ImportBeanDefinitionRegistrarConfig1 {

	public ImportBeanDefinitionRegistrarConfig1() {
		System.out.println("ImportBeanDefinitionRegistrarConfig 无参构造函数 执行");
	}
}
