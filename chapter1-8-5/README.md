#   wer 
    BeanPostProcessor接口：后置bean处理器，允许自定义修改新的bean实例，
    应用程序上下文可以在其bean定义中自动检测BeanPostProcessor类型的bean，并将它们应用于随后创建的任何bean。
    （例如：配置文件中注册了一个自定义BeanPostProcessor类型的bean，
    一个User类型的bean，应用程序上下文会在创建User实例之后对User应用BeanPostProcessor）。
  
  
  
    BeanFactoryPostProcessor接口：后置工厂处理器，允许自定义修改应用程序上下文的bean定义，
    调整bean属性值。应用程序上下文可以在其bean定义中自动检测BeanFactoryPostProcessor，
    并在创建任何非BeanFactoryPostProcessor类型bean之前应用它们
    （例如：配置文件中注册了一个自定义BeanFactoryPostProcessor类型的bean，
    一个User类型的bean，应用程序上下文会在创建User实例之前对User应用BeanFactoryPostProcessor）。
    
