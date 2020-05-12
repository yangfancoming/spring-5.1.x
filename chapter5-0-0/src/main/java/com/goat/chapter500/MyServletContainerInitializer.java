package com.goat.chapter500;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 * Created by Administrator on 2020/5/12.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/12---10:06
 */
@HandlesTypes(value = {IHelloService.class})
public class MyServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * 应用启动的时候，会运行onStartup方法；
	 * <p>
	 * Set<Class<?>> c：感兴趣的类型的所有子类型；
	 * ServletContext ctx:代表当前Web应用的ServletContext；一个Web应用一个ServletContext；
	 * <p>
	 */
	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
		//这里的c会把所有我们感兴趣的类型都拿到
		System.out.println("感兴趣的类型：");
		for (Class<?> claz : c) {
			System.out.println(claz);
		}
		//==========================编码形式注册三大组件============================
		////注册组件  ServletRegistration
		//ServletRegistration.Dynamic servlet = ctx.addServlet("userServlet", new UserServlet());
		////配置servlet的映射信息
		//servlet.addMapping("/user");
		//
		////注册Listener
		//ctx.addListener(UserListener.class);
		//
		////注册Filter  FilterRegistration
		//FilterRegistration.Dynamic filter = ctx.addFilter("userFilter", UserFilter.class);
		////配置Filter的映射信息
		//filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
	}
}

