#
    Mybatis整合spring，主要通过两种途径：一种是基于xml配置的，一种基于java config 配置。
    
    一种是基于xml配置的。这种方式通过解析xml，生成bean。主要是通过扩展spirng NamespaceHandlerSupport类型来实现自定义解析Mybatis的xml配置。
    Mybatis相关实现主要提现的类NamespaceHandler、MapperScannerBeanDefinitionParser。
    
    一种基于java config 配置。这种方式通过注解MapperScan，让spring容器扫描到注解。
    Mybatis相关实现主要体现的类：注解@interface  MapperScan、MapperScannerRegistrar。
    
    这两种方法的实现都是通过Mybatis的 【ClassPathMapperScanner】 类向容器中注册bean的。
    那这个ClassPathMapperScanner扫描到Mybatis的mapper接口，是如何注册实例的呢，接口又怎么会实例呢。
        
        
# spring-mybatis 整合流程   基于注解的方式 
    135 警告: 【IOC容器 处理 @ComponentScan 注解  --- 】 value属性： com.goat.chapter651
    169 警告: 【 IOC容器 bean实例化 】 beanName： org.mybatis.spring.annotation.MapperScannerRegistrar
    185 警告: 【mybatis 处理 @MapperScan 注解  ---   】 value属性值： com.goat.chapter651.dao
    205 警告: 【 IOC容器 bean实例化 】 beanName： org.mybatis.spring.mapper.MapperScannerConfigurer
    221 警告: 【mybatis】 使用父类doSan() 扫描并注册bean定义，basePackages：[com.goat.chapter651.dao]
    229	警告: 【mybatis】 Creating MapperFactoryBean with name 'bookMapper' and 'com.goat.chapter651.dao.BookMapper' mapperInterface
    303 警告: 【mybatis】 MapperFactoryBean 单参构造函数 执行
    305 警告: 【 IOC容器 bean实例化 】 beanName： org.mybatis.spring.mapper.MapperFactoryBean
    335	警告: 【mybatis】 构建 SqlSessionFactory 完毕 ： org.apache.ibatis.session.defaults.DefaultSqlSessionFactory@49070868
    349	警告: 【mybatis】 构建 SqlSessionTemplate 完毕 ： org.apache.ibatis.session.defaults.DefaultSqlSession@b7f23d9