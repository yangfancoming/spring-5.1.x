#   AOP 动态代理 实现调用栈

    createAopProxy:45, DefaultAopProxyFactory (org.springframework.aop.framework)
    createAopProxy:87, ProxyCreatorSupport (org.springframework.aop.framework)
    getProxy:94, ProxyFactory (org.springframework.aop.framework)
    createProxy:496, AbstractAutoProxyCreator (org.springframework.aop.framework.autoproxy)
    wrapIfNecessary:363, AbstractAutoProxyCreator (org.springframework.aop.framework.autoproxy)
    postProcessAfterInitialization:297, AbstractAutoProxyCreator (org.springframework.aop.framework.autoproxy)
    applyBeanPostProcessorsAfterInitialization:405, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
    initializeBean:1807, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
    doCreateBean:612, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
    createBean:494, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
    lambda$doGetBean$10:297, AbstractBeanFactory (org.springframework.beans.factory.support)
    getSingleton:251, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
    doGetBean:295, AbstractBeanFactory (org.springframework.beans.factory.support)
    getBean:1400, AbstractBeanFactory (org.springframework.beans.factory.support)
    preInstantiateSingletons:772, DefaultListableBeanFactory (org.springframework.beans.factory.support)
    finishBeanFactoryInitialization:840, AbstractApplicationContext (org.springframework.context.support)
    refresh:526, AbstractApplicationContext (org.springframework.context.support)
    <init>:71, AnnotationConfigApplicationContext (org.springframework.context.annotation)
    test2:38, App (com.goat.chapter400.annotation)