# 流程 

    init
    init 方法被设计成只调用一次。它在第一次创建 Servlet 时被调用，在后续每次用户请求时不再调用。因此，它是用于一次性初始化，就像 Applet 的 init 方法一样。
    Servlet 创建于用户第一次调用对应于该 Servlet 的 URL 时，但是您也可以指定 Servlet 在服务器第一次启动时被加载。
    当用户调用一个 Servlet 时，就会创建一个 Servlet 实例，每一个用户请求都会产生一个新的线程，适当的时候移交给 doGet 或 doPost 方法。init() 方法简单地创建或加载一些数据，这些数据将被用于 Servlet 的整个生命周期。
   
   
    service
    service() 方法是执行实际任务的主要方法。Servlet 容器（即 Web 服务器）调用 service() 方法来处理来自客户端（浏览器）的请求，并把格式化的响应写回给客户端。
    每次服务器接收到一个 Servlet 请求时，服务器会产生一个新的线程并调用服务。service() 方法检查 HTTP 请求类型（GET、POST、PUT、DELETE 等），并在适当的时候调用 doGet、doPost、doPut，doDelete 等方法。
   
   
    doGet和doPost
    GET 请求来自于一个 URL 的正常请求，或者来自于一个未指定 METHOD 的 HTML 表单，它由 doGet() 方法处理。
    POST 请求来自于一个特别指定了 METHOD 为 POST 的 HTML 表单，它由 doPost() 方法处理。
  
  
    destroy
    destroy() 方法只会被调用一次，在 Servlet 生命周期结束时被调用。destroy() 方法可以让您的 Servlet 关闭数据库连接、停止后台线程、把 Cookie 列表或点击计数器写入到磁盘，并执行其他类似的清理活动。
    在调用 destroy() 方法之后，servlet 对象被标记为垃圾回收。
    
    整体流程
    
    
    第一个到达服务器的 HTTP 请求被委派到 Servlet 容器。
    Servlet 容器在调用 service() 方法之前加载 Servlet。
    然后 Servlet 容器处理由多个线程产生的多个请求，每个线程执行一个单一的 Servlet 实例的 service() 方法。
    


#  req res  HttpSession ServletConfig ServletContext 的作用域和生命周期 
     HttpServletRequest,HttpServletResponse:这两个属性的作用范围最小。
     时间上：只是本身请求和应答完成就失效，当然转发是把当前的request对象取出来传给另一个资源，其实本身的request对象还是只生存到本次请求结束，response也同样。
     空间上：只能发送请求的客户端有效。
    
     HttpSession:一次连结到客户端关闭，时间作用范围比上面两个大，空间任用范围相同。
    
     ServletConfig:从一个servlet被实例化后，对任何客户端在任何时候访问有效，但仅对本servlet有效，一个servlet的ServletConfig对象不能被另一个servlet访问。
    
     ServletContext:对任何servlet，任何人在任何时间都有效，这才是真正全局的对象。
     
# <url-pattern>/*</url-pattern>  映射规则

    对于如下的一些映射关系：
    　　Servlet1 映射到 /abc/* 
    　　Servlet2 映射到 /* 
    　　Servlet3 映射到 /abc 
    　　Servlet4 映射到 *.do 
    问题：
    　　当请求URL为“/abc/a.html”，“/abc/*”和“/*”都匹配，哪个servlet响应
        　　Servlet引擎将调用Servlet1。
    　　当请求URL为“/abc”时，“/abc/*”和“/abc”都匹配，哪个servlet响应
        　　Servlet引擎将调用Servlet3。
    　　当请求URL为“/abc/a.do”时，“/abc/*”和“*.do”都匹配，哪个servlet响应
        　　Servlet引擎将调用Servlet1。
    　　当请求URL为“/a.do”时，“/*”和“*.do”都匹配，哪个servlet响应
        　　Servlet引擎将调用Servlet2。
    　　当请求URL为“/xxx/yyy/a.do”时，“/*”和“*.do”都匹配，哪个servlet响应
        　　Servlet引擎将调用Servlet2。
    　　匹配的原则就是"谁长得更像就找谁"
    
    
    缺省Servlet
    　　如果某个Servlet的映射路径仅仅为一个正斜杠（/），那么这个Servlet就成为当前Web应用程序的缺省Servlet。