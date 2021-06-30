package com.goat.chapter201.componentscan.item03;

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
@Configuration
@ComponentScans(value = { @ComponentScan(
		value="com.goat.chapter201.common",
		// 扫描结果中排除掉带有 @Service 注解的bean
		excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Service.class),
		}
)})
public class ComponentScansExcludeFiltersConfig {


}
