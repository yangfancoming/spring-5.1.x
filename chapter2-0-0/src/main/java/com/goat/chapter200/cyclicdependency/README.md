#
    谈到Spring Bean的循环依赖，有的小伙伴可能比较陌生，毕竟开发过程中好像对循环依赖这个概念无感知。
    其实不然，你有这种错觉，权是因为你工作在Spring的襁褓中，从而让你“高枕无忧”~
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
     
     Spring的循环依赖的理论依据基于Java的引用传递，当获得对象的引用时，对象的属性是可以延后设置的。（但是构造器必须是在获取引用之前，毕竟你的引用是靠构造器给你生成的，儿子能先于爹出生？哈哈）