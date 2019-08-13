# 
    1. web.xml 指定 application.xml 
    2. application.xml 指定 扫描位置
    2. application.xml 指定 视图解析器
    
    
# 源码 流程
    DispatcherServlet--->service(HttpServletRequest request, HttpServletResponse response)  实际上是父类 FrameworkServlet 的
    FrameworkServlet--->processRequest--->doService
    DispatcherServlet--->doService  在doService方法中对Request参数进行处理，然后调用doDispatch方法
    DispatcherServlet--->doDispatch(request, response);  
    在doDispatch方法中获取并调用处理器映射器、处理器适配器，获取并返回执行结果。
    
    
    
    1.Tomcat 启动，对 DispatcherServlet 进行实例化，然后调用它的 父类的 HttpServletBean init() 方法进行初始化，在这个初始化过程中完成了：
         1.对 web.xml 中初始化参数的加载；
         2.建立 WebApplicationContext (SpringMVC的IOC容器)；
         3.进行组件的初始化；
    2.客户端发出请求，由 Tomcat 接收到这个请求，如果匹配 DispatcherServlet 在 web.xml 中配置的映射路径，Tomcat 就将请求转交给 DispatcherServlet 处理；
    3.DispatcherServlet 从容器中取出所有 HandlerMapping 实例（每个实例对应一个 HandlerMapping 接口的实现类）并遍历，每个 HandlerMapping 会根据请求信息，通过自己实现类中的方式去找到处理该请求的 Handler (执行程序，如Controller中的方法)，并且将这个 Handler 与一堆 HandlerInterceptor (拦截器) 封装成一个 HandlerExecutionChain 对象，一旦有一个 HandlerMapping 可以找到 Handler 则退出循环；
    4.DispatcherServlet 取出 HandlerAdapter 组件，根据已经找到的 Handler，再从所有 HandlerAdapter 中找到可以处理该 Handler 的 HandlerAdapter 对象；
    5.执行 HandlerExecutionChain 中所有拦截器的 preHandler() 方法，然后再利用 HandlerAdapter 执行 Handler ，执行完成得到 ModelAndView，再依次调用拦截器的 postHandler() 方法；
    6.利用 ViewResolver 将 ModelAndView 或是 Exception（可解析成 ModelAndView）解析成 View，然后 View 会调用 render() 方法再根据 ModelAndView 中的数据渲染出页面；
    7.最后再依次调用拦截器的 afterCompletion() 方法，这一次请求就结束了。