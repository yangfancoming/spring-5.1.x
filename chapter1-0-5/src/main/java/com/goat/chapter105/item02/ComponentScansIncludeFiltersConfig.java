package com.goat.chapter105.item02;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 ComponentScans   includeFilters
 * @ author  山羊来了
 * @ date 2020/3/17---13:48
 */
@Configuration // 该注解就相当于传统的xml文件
@ComponentScans(value = { @ComponentScan(
		value="com.goat.chapter105.common",
		//includeFilters 是扫描结果是只包含，includeFilters 若想生效必须配置 useDefaultFilters = false ！
		includeFilters = {
				@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Service.class),
		},useDefaultFilters = false
)})
public class ComponentScansIncludeFiltersConfig {

}
