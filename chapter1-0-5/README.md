#  注解目录
    @DependsOn 控制bean加载顺序 应运而生
    
    可能有些场景中，bean A 间接依赖 bean B。如Bean B应该需要更新一些全局缓存，可能通过单例模式实现且没有在spring容器注册，bean A需要使用该缓存；因此，如果bean B没有准备好，bean A无法访问。
    
    另一个场景中，bean A是事件发布者（或JMS发布者），bean B (或一些) 负责监听这些事件，典型的如观察者模式。我们不想B 错过任何事件，那么B需要首先被初始化。
    
    简言之，有很多场景需要bean B应该被先于bean A被初始化，从而避免各种负面影响。我们可以在bean A上使用@DependsOn注解，告诉容器bean B应该先被初始化。下面通过示例来说明。


#  item01   传统xml方式获取bean 及 扫描bean  
  
# item02    注解方式获取bean 及 扫描bean  @bean   @Scope("prototype")   @ComponentScan

# itme03    @Configuration 注解学习
# itme04    @Import ImportSelector ImportBeanDefinitionRegistrar   等注解学习
# itme05    FactoryBean
# itme06    @Value 属性注入
# itme07    @Autowire 自动装配