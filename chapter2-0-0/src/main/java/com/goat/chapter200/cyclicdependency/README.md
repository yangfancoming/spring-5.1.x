#
    谈到Spring Bean的循环依赖，有的小伙伴可能比较陌生，毕竟开发过程中好像对循环依赖这个概念无感知。
    其实不然，你有这种错觉，全是因为你工作在Spring的襁褓中，从而让你“高枕无忧”~
    我十分坚信，小伙伴们在平时业务开发中一定一定写过如下结构的代码：
     
    @Service
    public class AServiceImpl implements AService {
        @Autowired
        private BService bService;
        ...
    }
    @Service
    public class BServiceImpl implements BService {
        @Autowired
        private AService aService;
        ...
    }
    这其实就是Spring环境下典型的循环依赖场景。
    但是很显然，这种循环依赖场景，Spring已经完美的帮我们解决和规避了问题。所以即使平时我们这样循环引用，也能够整成进行我们的coding之旅~
    
    
# Spring中三大循环依赖场景演示
    item02  构造器注入
    item03  field属性注入（setter方法注入）循环依赖
    item04  prototype field属性注入循环依赖
    
    
# 对于Spring循环依赖的情况总结如下：
    
    不能解决的情况：
     1. 构造器注入循环依赖
     2. prototype field属性注入循环依赖
    能解决的情况：
     1. field属性注入（setter方法注入）循环依赖
     
     Spring的循环依赖的理论依据，基于Java的引用传递，当获得对象的引用时，对象的属性是可以延后设置的。（但是构造器必须是在获取引用之前，毕竟你的引用是靠构造器给你生成的，儿子能先于爹出生？哈哈）
     
# 伪代码：
      一 先获取bean A
      {
      1.1 getBean A
      1.2 doGetBean A
      1.3 获取早期对象 getSingleton A，第一次没有A的早期对象。
      1.4 createBean
      1.5 doCreateBean{
        1.5.1 创建bean实例
        1.5.2 暴露早期对象，这个时候有A的早期对象了。
        1.5.3 属性填充，这个时候会发现依赖了bean B，去getBean B，执行完getBeanB之后完成属性填充。
        1.5.4 初始化bean A
      }
      返回A
      }
      
      二 getBean B
      {
      2.1 getBean B
      2.2 doGetBean B
      2.3 获取早期对象 getSingleton B，第一次获取B，还没有B的早期对象。
      2.4 createBean B
      2.5 doCreateBean B {
         2.5.1 创建bean实例
         2.5.2 暴露B的早期对象
         2.5.3 属性填充，发现依赖了A，去getBean A，第二次获取A可以拿 到一个早期对象A。属性填充完毕，往下走。
         2.5.4 初始化beanB
      }
      2.6 返回创建好的B
      }
    