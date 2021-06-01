

# 有状态 Bean 和无状态 Bean 的对象区别
    1、有状态就是有数据存储功能。有状态对象(Stateful Bean)，就是有实例变量的对象，可以保存数据，是非线程安全的。在不同方法调用间不保留任何状态。 
    2、无状态就是一次操作，不能保存数据。无状态对象(Stateless Bean)，就是没有实例变量的对象.不能保存数据，是不变类，是线程安全的。
    
# Bean 的作用域
    在Spring Framework中，总共定义了6种bean 的作用域，其中有4种作用域只有当应用为web应用的时候才有效，并且Spring还支持自定义作用域。
    1.1 singleton作用域
    singleton作用域表示在整个Spring容器中一个bean定义只生成了唯一的一个bean实例，被Spring容器管理。所有对这个bean的请求和引用都会返回这个bean实例。
    singleton作用域是Spring中默认的作用域，可以在定义bean的时候指定或者不指定都可以
    
    1.2 prototype作用域
    prototype作用域表示的是一个bean定义可以创建多个bean实例，有点像一个类可以new多个实例一样。
    也就是说，当注入到其他的bean中或者对这个bean定义调用getBean()时，都会生成一个新的bean实例。
    作为规则，应该对所有有状态的bean指定prototype作用域，对所有无状态的bean指定singleton作用域。
    
    1.3 当singleton的bean依赖prototype的bean
    当singleton的bean依赖prototype的bean时，请注意，这个依赖关系是在实例化时候解析的，并且只解析一次。因此，每个依赖的prototype的bean都是一个新的bean实例。
    然而，如果一个singleton的bean想要在运行时，在每次注入时都能有一个新的prototype的bean生成并注入，这是不行的。
    因为依赖注入在初始化的时候只会注入一次。如果想要在运行时多次注入新的prototype的bean
    
    
    1.4 request、session、application、websocket作用域
    request、session、application、websocket作用域只有在web环境下才有用。


#  1.Bean的作用域
   
    singleton：单例Bean只在容器中存在一个实例，在Spring内部通过HashMap来维护单例bean的缓存
    prototype：每次索取bean时都会创建一个全新的Bean
    request：每次请求都会创建一个全新Bean，该类型作用于Web类型的Spring容器
    session：每个会话创建一个全新Bean，该类型作用于Web类型的Spring容器
    globalSession：类似于session作用域，只是其用于portlet环境的web应用。如果在非portlet环境将视为session作用域
    总结：以上就是spring中bean的作用域，其中singleton，prototype属于Spring bean的基本作作用域，request，session，globalSession属于web应用环境的作用域，必须有web应用环境的支持

# 2.Bean的生命周期
    IoC容器启动
    实例化bean
    如果Bean实现了 BeanNameAware 接口，则调用setBeanName(String name)返回beanName，该方法不是设置beanName，而只是让Bean获取自己在BeanFactory配置中的名字
    如果Bean实现BeanFactoryAware接口，会回调该接口的setBeanFactory(BeanFactory beanFactory)方法，传入该Bean的BeanFactory，这样该Bean就获得了自己所在的BeanFactory
    如果Bean实现了ApplicationContextAware接口，则调用该接口的setApplicationContext(ApplicationContext  applicationContext)方法，设置applicationContext
    如果有Bean实现了BeanPostProcessor接口，则调用该接口的postProcessBeforeInitialzation(Object bean，String beanName)方法，将此BeanPostProcessor应用于给定的新bean实例
    如果Bean实现了InitializingBean接口，则会回调该接口的afterPropertiesSet()方法
    如果Bean配置了init-method方法，则会执行init-method配置的方法
    如果Bean实现了BeanPostProcessor接口，则会回调该接口的postProcessAfterInitialization(Object bean，String beanName)方法
    到此为止，spring中的bean已经可以使用了，这里又涉及到了bean的作用域问题，对于singleton类型的bean，Spring会将其缓存;对于prototype类型的bean，不缓存，每次都创建新的bean的实例
    容器关，如果Bean实现了DisposableBean接口，则会回调该接口的destroy()方法销毁bean，
    如果用户配置了定destroy-method，则调用自定义方法销毁bean
    

