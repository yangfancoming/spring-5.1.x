# Spring 自动注入三种方式
    ①ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory(); =>得到Map<String, BeanDefinition> (XML方式和注解配置)
    ②invokeBeanFactoryPostProcessors(beanFactory)=>得到Map<String, BeanDefinition> (JAVA配置)
    ③finishBeanFactoryInitialization(beanFactory);=>实例化
