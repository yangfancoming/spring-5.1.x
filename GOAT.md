# 目录 
    chapter1-0-1  为什么需要 spring  ioc di 这种思想  即 Spring IOC 应运而生
    chapter1-0-3  Spring 资源文件读取
    chapter1-0-5  spring 注解学习
    chapter1-1-0  spring Bean的生命周期 注解 方式
    chapter1-1-5  spring Bean的生命周期 xml  方式    ？生命周期回调 ？
  
    
    chapter1-2-0  spring 自定义标签/ 自定义命名空间  Spring解析xml默认命名空间
    chapter1-8-0  Spring 实例化Bean的三种方式
    chapter1-8-2  Spring BeanFactory和FactoryBean的区别
    chapter1-8-5  Spring BeanPostProcessor和BeanFactoryPostProcessor的区别
    
    chapter2-0-0  spring Bean的装配 （依赖注入）  四种方式
    chapter2-0-1  spring Bean的装配 xml  方式
                    <bean> 标签属性示例
                                1.alias 别名标签
                                2.autowire 自动装配 
                                
    chapter2-0-2  spring Bean的装配 注解 方式
    chapter2-1-0  spring bean装配 源码示例  BeanFactoryAware BeanPostProcessor  FactoryBean
# aop 模块   
    chapter4-0-0  spring AOP
    chapter4-0-4  spring AOP 实现自定义的TargetSource
# web 模块
    chapter5-0-0  spring web
    chapter5-0-5  spring Servlet 
    chapter5-1-0  spring web  注解版 
    chapter5-1-5  spring web  web.xml + xml 配置
    chapter5-1-6  SpringMVC源码阅读环境搭建  
    
    chapter5-2-1  处理器映射器 HandlerMapping  SimpleUrlHandlerMapping
    chapter5-2-2  处理器映射器 HandlerMapping  BeanNameUrlHandlerMapping
    chapter5-2-5  定位Controller  解读SpringMVC如何找到我们定义的Controller和Controller中的方法，比如路径是如何映射的，弄清楚Controller是如何匹配我们传入的参数，并定义简单的参数解析器
    chapter5-2-8  Controller中参数解析   解读Controller方法中的参数解析和参数解析类
    
    chapter5-3-0  Json，Xml自动转换   解读SpringMVC的Json和Xml的转换原理
    chapter5-3-3  属性编辑器、数据绑定    解读SpringMVC数据绑定机制和属性编辑器，介绍BeanWrapper，并自定义属性编辑器
    chapter5-3-6  拦截器     解读SpringMVC拦截器的工作原理并自定义拦截器
    chapter5-3-9  过滤器    解读SpringMVC过滤器的工作原理，并自定义过滤器，让你弄清楚过滤器和拦截器的区别
    chapter5-4-0  视图解析器      即使前后端分离大行其道，视图解析器作为Spring源码的一部分，我们还是要研读。该文解读SpringMVC如何完成视图解析，提供Freemarker和Jsp的共存方案
    chapter5-4-5  异常解析器      解读SpringMVC如何完成异常解析、捕捉异常，并自定义异常和异常解析器
 


    chapter5-8-7  语言环境 SessionLocaleResolver  
    chapter5-8-8  语言环境 LocaleResolver (国际化)
    chapter5-8-9  语言环境 CookieLocaleResolver  
    
# JDBC 模块 
    chapter6-0-0  spring jdbc
    chapter6-0-2  spring jdbc  编程式事务
    chapter6-0-4  spring jdbc  基于tx标签 
    chapter6-0-6  spring jdbc  基于 @Transactional注解的声明式
    
# spring Mybatis  模块    
    chapter6-5-0  spring mybatis
    chapter6-5-1  spring mybatis
    
    chapter7-0-0  spring web MVC 高级
    chapter8-0-0  spring web flow


# 源码分析思路：
    首先：确定IOC容器做了什么，其实主要是加载和管理bean
    然后：找到做这些事情的所有顶层接口
    再次：找顶层接口的干不同事情的子类、分析这些子类是做什么的
    最后：挑最富有内涵的接口分析其原理和思想，学习他们的设计思路

#  bean的几种形态： 概念态、内存态、纯静态、成熟态
 
    形态一：xml或者注解标注的概念态，此时bean只是一个由类和一些描述文件定义的概念状态，比如：
    
    <bean id="schedulerFactoryBean"
            class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
            <property name="jobFactory" ref="jobFactory"></property>
    </bean>
    
    
    形态二：内存态，此时Bean被加载到内存中，但还处在BeanDefinition这种定义状态，这种状态实际上是bean创建的模板。  内存态的bean 好比是设计图纸  根据设计图纸 来创建  纯静态的bean 
    
    形态三：纯净态，此时的Bean只是被Bean工厂创建成了对象，但是并没有给bean的每个属性赋值，此时各属性还处于0 null false等这种初始状态，想想无参构造函数创建的对象。
    
    形态四：成熟态，纯净的Bean的属性被赋予真实有效的值，此时的Bean就是我们最终要使用的状态，已经可以提供正常服务了。
    
# bean相关重要接口
    接口一：Resource接口，能把xml等文件读取到内存中，并能够获取到xml文件的详细信息。
    
    接口二：Document接口，用于xml文件解析到内存，类似于js里的dom解析。
    
    接口三：BeanDefinitionReader接口，用于解析xml和加载Bean成为BeanDefinition
    
    接口四：BeanDefinition接口，bean的抽象定义，如是否单例，是否是懒加载，有哪些重要属性等等。
    
    接口五：BeanFactory，Spring里面的重要模式-工厂模式，用于创建真正意义上的bean类。
    
    接口六：ApplicationContext，应用程序上下文，我一般都把上下文理解成容器，这个容器就是bean真正运行的容器，spring机器也由此启动。
    
# 创建步骤

    步骤一：资源以统一资源接口Resource加载入内存，即利用Resource接口及其子类把xml加载到内存中。（形态一）
    
    步骤二：把Resource处理成Document，然后根据Dom接口处理Element节点即可，然后校验，校验过程根据DTD和XSD也就是ApplicationContext头部的配置进行xml格式校验
    
    步骤三：解析，解析过程包括bean的id，property以及其他方面的配置，把资源中的bean和其他标签（是否懒加载，是否单例等）解析成BeanDefinition格式（形态二）
    
    步骤四：注册、就是放到全局的Map中，不过注册之前做了重名校验，注册的方式有别名和bean名两种
    
    步骤五：解析注册完成之后通知监听器，这里只为后期扩展用，程序员可以实现相关监听器方法对这个事件进行处理
    
    步骤六：加载，加载步骤：注意：此时加载的Bean还没有被装载数据，还处于（形态三）纯净态
    （1）：转换对应的beanname,也就是获取最终的beanname，别名转换，去掉无效标识符等
    （2）：尝试从缓存中加载单例
    （3）：Bean的实例化
    （4）：原型模式的依赖检查
    （5）：检测ParentBeanFactory，如果当前检测到的xml文件中不包含BeanName，就去父Factory中获取
    （6）：将存储XML配置文件的GernericBeanDefinition转换为RootBeanDefinition
    （7）：寻找依赖，先加载依赖的Bean
    （8）：针对不同的scope进行bean的创建singleton，request等
    （9）：类型转换
    
    步骤七：步骤六弄完bean就是成熟态了（形态四）
    



