# Spring 自动注入三种方式
    ①ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory(); =>得到Map<String, BeanDefinition> (XML方式和注解配置)
    ②invokeBeanFactoryPostProcessors(beanFactory)=>得到Map<String, BeanDefinition> (JAVA配置)
    ③finishBeanFactoryInitialization(beanFactory);=>实例化

    总结：
    1、@Autowired 是通过 byType 的方式去注入的， 使用该注解，要求接口只能有一个实现类。
    2、@Resource 可以通过 byName 和 byType的方式注入， 默认先按 byName的方式进行匹配，如果匹配不到，再按 byType的方式进行匹配。
    3、@Qualifier 注解可以按名称注入， 但是注意是 类名。
