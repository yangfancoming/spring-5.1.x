# web 启动流程 总结

    1.容器启动时，加载web.xml部署描述文件，扫描到并找到DispatcherServlet核心控制器
    
    2.调用HttpServletBean的init()方法，把DispatcherServlet初始化参数设置到DispatcherServlet中，
        并调用子类FrameworkServlet的initServletBean()方法
    
    3.FrameworkServlet的initServletBean()创建Spring MVC容器并初始化，并且和Spring父容器进行关联，
        使得Spring MVC容器能访问Spring容器中定义的bean，之后调用子类DispatcherServlet的onRefresh()方法
   
    4.DispatcherServlet的onRefresh(ApplicationContext context)对DispatcherServlet的策略组件进行初始化