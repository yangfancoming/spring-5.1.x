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
    如果Bean实现了BeanNameAware接口，则调用setBeanName(String name)返回beanName，该方法不是设置beanName，而只是让Bean获取自己在BeanFactory配置中的名字
    如果Bean实现BeanFactoryAware接口，会回调该接口的setBeanFactory(BeanFactory beanFactory)方法，传入该Bean的BeanFactory，这样该Bean就获得了自己所在的BeanFactory
    如果Bean实现了ApplicationContextAware接口，则调用该接口的setApplicationContext(ApplicationContext  applicationContext)方法，设置applicationContext
    如果有Bean实现了BeanPostProcessor接口，则调用该接口的postProcessBeforeInitialzation(Object bean，String beanName)方法，将此BeanPostProcessor应用于给定的新bean实例
    如果Bean实现了InitializingBean接口，则会回调该接口的afterPropertiesSet()方法
    如果Bean配置了init-method方法，则会执行init-method配置的方法
    如果Bean实现了BeanPostProcessor接口，则会回调该接口的postProcessAfterInitialization(Object bean，String beanName)方法
    到此为止，spring中的bean已经可以使用了，这里又涉及到了bean的作用域问题，对于singleton类型的bean，Spring会将其缓存;对于prototype类型的bean，不缓存，每次都创建新的bean的实例
    容器关，如果Bean实现了DisposableBean接口，则会回调该接口的destroy()方法销毁bean，
    如果用户配置了定destroy-method，则调用自定义方法销毁bean
    
