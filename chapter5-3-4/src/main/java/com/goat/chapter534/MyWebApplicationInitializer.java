package com.goat.chapter534;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by 64274 on 2019/8/14.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/14---10:13
 */
public class MyWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	/**
	 *  返回Spring应用根容器中定义的beans，对应ContextLoaderListener，是Spring根容器
	 */
	@Nullable
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return null;
	}

	/**
	 *  返回Spring MVC应用容器中定义的beans，对应DispatcherServlet中加载的bean
	 *  Spring MVC容器是根容器的子容器，子容器可以看到根容器中定义的beans，反之不行
	 */
	@Nullable
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[]{ WebConfig.class };
	}

	/**
	 *  指定映射拦截URLs
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[]{"/"};
	}

	/**
	 *  通过重写此方法修改DispatcherServlet的名称，对应<servlet-name></servlet-name>标签
	 */
	@Override
	protected String getServletName() {
		return "dispatcher";
	}
}

