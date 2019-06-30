package com.goat.spring.demo.annotation.item02;

import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: 其中要注意的是这里要使用AnnotatedElementUtils，如果还是用 AnnotationUtils 会发现继承不起作用，这个在AnnotationUtils类的英文文档中也有说明
 * @ author  山羊来了
 * @ date 2019/6/30---14:41
 */
public class App {

	public static void main(String[] args) {
//		AnnotaionBase annotaionBase = AnnotationUtils.findAnnotation(ExtendClass.class, AnnotaionBase.class);
		AnnotaionBase annotaionBase = AnnotatedElementUtils.findMergedAnnotation(ExtendClass.class, AnnotaionBase.class);
		AnnotaionBase annotaionBase2 = AnnotatedElementUtils.findMergedAnnotation(ExtendClass2.class, AnnotaionBase.class);
		System.out.println(annotaionBase.value());
		System.out.println(annotaionBase2.value());
		System.out.println(annotaionBase2.haha());

	}
}
