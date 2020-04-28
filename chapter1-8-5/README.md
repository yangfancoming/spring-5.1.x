
#   item01   BeanFactoryPostProcessor, Ordered 
                BeanFactoryPostProcessor 接口：后置工厂处理器，允许自定义修改应用程序上下文的bean定义，
                调整bean属性值。应用程序上下文可以在其bean定义中自动检测BeanFactoryPostProcessor，
                并在创建任何非BeanFactoryPostProcessor类型bean之前应用它们
                （例如：配置文件中注册了一个自定义BeanFactoryPostProcessor类型的bean，
                一个User类型的bean，应用程序上下文会在创建User实例之前对User应用BeanFactoryPostProcessor）。
                
#   item02   BeanPostProcessor, Ordered 
                BeanPostProcessor 接口：后置bean处理器，允许自定义修改新的bean实例，
                应用程序上下文可以在其bean定义中自动检测BeanPostProcessor类型的bean，并将它们应用于随后创建的任何bean。
                （例如：配置文件中注册了一个自定义BeanPostProcessor类型的bean，
                一个User类型的bean，应用程序上下文会在创建User实例之后对User应用BeanPostProcessor）。
                
#    item03 生命周期
     
#    item04 spring.profiles 配置


#    item05 
    
        FactoryBean 接口：可以返回bean的实例的工厂bean，通过实现该接口可以对bean进行一些额外的操作，
                例如根据不同的配置类型返回不同类型的bean，简化xml配置等。
                在使用上也有些特殊，BeanFactory接口中有一个字符常量String FACTORY_BEAN_PREFIX = "&"; 
                当我们去获取BeanFactory类型的bean时，
                如果beanName不加&则获取到对应bean的实例；
                如果beanName加上&，则获取到BeanFactory本身的实例；
                FactoryBean接口对应Spring框架来说占有重要的地位，Spring本身就提供了70多个FactoryBean的实现。
                他们隐藏了实例化一些复杂的细节，给上层应用带来了便利。从Spring3.0开始，FactoryBean开始支持泛型。
                
        BeanFactory 接口：IoC容器的顶级接口，是IoC容器的最基础实现，也是访问Spring容器的根接口，负责对bean的创建，访问等工作。