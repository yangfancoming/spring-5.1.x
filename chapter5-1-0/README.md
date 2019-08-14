# 报错  
        Error:(14, 8) java: 无法访问javax.servlet.ServletException
        找不到javax.servlet.ServletException的类文件
        
        加入     optional("javax.servlet:javax.servlet-api:4.0.1")  依赖
        
# 总结
    本demo示例 以java配置的方式，通过WebApplicationInitializer配置Spring MVC（替换传统web.xml方式），
    实现了一个rest风格的服务，此方式是Spring3.1之后引入配置方式，使用Servlet3.0技术规范，
    在Servlet3.0+中web容器启动(Tomcat)时，扫描类路径下所有的WebApplicationInitializer接口。