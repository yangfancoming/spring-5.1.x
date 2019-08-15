# 引入
    1.dubbo服务启动第一步就是需要将服务注册到注册中心，
    这里读取了大量的spring自定义标签，比如<dubbo:service>等等，
    正好先看一下Spring自定义标签的扩展功能。
    
    2.Spring源码中也大量使用了自定义标签，比如spring的AOP的定义，其标签为<aspectj-autoproxy />
    
# Spring自定义标签总共可以分为以下几个步骤 
    ① 定义Bean 标签解析生成接收配置的POJO。 User
    ② 定义schema文件，定义自定义标签的attr属性 spring-user.xsd
    ③ 定义解析类parser，遇到自定义标签如何解析。 UserBeanDefinitionParser
    ④ 定义命名空间处理类namespaceSupport，遇到自定义的命名标签，能够路由到对应的解析类。 UserNamespaceHandler
    ⑤ 声明schema，写入spring.schema文件中  spring.schemas
    ⑥ 声明自定义标签的命名处理类namespaceHandler,写入spring.handlers文件中 spring.handlers
    
    
    
    
   
    
