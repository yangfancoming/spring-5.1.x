#   
    BeanFactory接口：IoC容器的顶级接口，是IoC容器的最基础实现，也是访问Spring容器的根接口，负责对bean的创建，访问等工作。
    
    FactoryBean接口：可以返回bean的实例的工厂bean，通过实现该接口可以对bean进行一些额外的操作，
            例如根据不同的配置类型返回不同类型的bean，简化xml配置等。在使用上也有些特殊，
            BeanFactory接口中有一个字符常量String FACTORY_BEAN_PREFIX = "&"; 
            当我们去获取BeanFactory类型的bean时，如果beanName不加&则获取到对应bean的实例；
            如果beanName加上&，则获取到BeanFactory本身的实例；
            FactoryBean接口对应Spring框架来说占有重要的地位，Spring本身就提供了70多个FactoryBean的实现。
            他们隐藏了实例化一些复杂的细节，给上层应用带来了便利。从Spring3.0开始，FactoryBean开始支持泛型。

