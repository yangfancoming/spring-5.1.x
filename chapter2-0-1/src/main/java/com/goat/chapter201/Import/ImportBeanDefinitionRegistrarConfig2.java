package com.goat.chapter201.Import;


import com.goat.chapter201.model.Blue;
import com.goat.chapter201.model.Red;
import org.springframework.context.annotation.Import;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---17:08
 */
@Import({Blue.class, Red.class,MyImportBeanDefinitionRegistrar.class})
public class ImportBeanDefinitionRegistrarConfig2 {

	public ImportBeanDefinitionRegistrarConfig2() {
		System.out.println("ImportBeanDefinitionRegistrarConfig 无参构造函数 执行");
	}
}
