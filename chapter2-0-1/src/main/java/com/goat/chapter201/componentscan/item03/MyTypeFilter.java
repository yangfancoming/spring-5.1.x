package com.goat.chapter201.componentscan.item03;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---15:46
 */
public class MyTypeFilter implements TypeFilter {

	/**
	 * metadataReader：读取到的当前正在扫描的类的信息
	 * metadataReaderFactory:可以获取到其他任何类信息的
	 */
	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) {
		//获取当前类注解的信息
		System.out.println(metadataReader.getAnnotationMetadata());
		//获取当前类资源（类的路径）
		System.out.println(metadataReader.getResource());
		//获取当前正在扫描的类的类信息
		ClassMetadata classMetadata = metadataReader.getClassMetadata();
		String className = classMetadata.getClassName();
		System.out.println("--->"+className);
		if(className.contains("Filter")){
			return true;
		}
		return false;
	}
}
