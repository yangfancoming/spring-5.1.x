package com.goat.chapter105.item02;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 ComponentScans   excludeFilters
 * @ author  山羊来了
 * @ date 2020/3/17---13:48
 */
@Configuration // 该注解就相当于传统的xml文件
@ComponentScans(value = { @ComponentScan(
		value="com.goat.chapter105.common",
		//扫描结果中排除掉带有 @Service 注解的bean，相反 includeFilters 是扫描结果是只包含
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Service.class),
		}
)})
public class ComponentScansExcludeFiltersConfig {


}