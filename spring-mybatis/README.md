#
    Mybatis整合spring，主要通过两种途径：一种是基于xml配置的，一种基于java config 配置。
    
    一种是基于xml配置的。这种方式通过解析xml，生成bean。主要是通过扩展spirng NamespaceHandlerSupport类型来实现自定义解析Mybatis的xml配置。
    Mybatis相关实现主要提现的类NamespaceHandler、MapperScannerBeanDefinitionParser。
    
    一种基于java config 配置。这种方式通过注解MapperScan，让spring容器扫描到注解。
    Mybatis相关实现主要体现的类：注解@interface  MapperScan、MapperScannerRegistrar。
    
    
        这两种方法的实现都是通过Mybatis的 【ClassPathMapperScanner】 类向容器中注册bean的。
        那这个ClassPathMapperScanner扫描到Mybatis的mapper接口，是如何注册实例的呢，接口又怎么会实例呢。