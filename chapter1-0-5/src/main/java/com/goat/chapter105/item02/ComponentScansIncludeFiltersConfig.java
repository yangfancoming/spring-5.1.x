package com.goat.chapter105.item02;

import com.goat.chapter105.MyTypeFilter;
import com.goat.chapter105.common.TestDummy;
import org.springframework.context.annotation.ComponentScan;
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
@ComponentScan(
		value="com.goat.chapter105.common",
		//includeFilters 是扫描结果是只包含，includeFilters 若想生效必须配置 useDefaultFilters = false ！
		includeFilters = {
				// 只加载包含有@Service注解的bean
				@ComponentScan.Filter(type = FilterType.ANNOTATION,classes = Service.class),
				// 加载指定bean(不管其是否有controller,service,component等注解均会被加载)
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,classes = TestDummy.class),
				// 自定义规则加载 sos 该种方式 已满足ANNOTATION条件的 不会进入该过滤器 eg: TestService 不会进入MyTypeFilter
				@ComponentScan.Filter(type = FilterType.CUSTOM,classes = MyTypeFilter.class),
		},useDefaultFilters = false
)
public class ComponentScansIncludeFiltersConfig {

}
