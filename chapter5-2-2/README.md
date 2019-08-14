# 总结
    本文分析了BeanNameUrlHandlerMapping类，如果看过上篇文章就发现，
    SimpleUrlHandlerMapping与BeanNameUrlHandlerMapping都实现HandlerMapping接口，
    即处理url与handler的映射，只是处理的策略不同而已。
    
    BeanNameUrlHanderlMapping有如下不足：
    处理器bean的id/name为一个url请求路径，前面有"/"，怪怪的；
    如果多个url映射同一个处理器bean，那么就需要定义多个bean，导致容器创建多个处理器实例，占用内存空间；
    处理器bean定义与url请求耦合在一起。