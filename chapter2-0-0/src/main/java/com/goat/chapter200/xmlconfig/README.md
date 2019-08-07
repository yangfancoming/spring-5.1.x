# xml 配置总结：
    构造方法注入对应：  <constructor-arg ref="compactDisc" />
    setter方法注入对应：      <property name="cd" ref="compactDisc" />
    1. 类头部并没有 @Component 注解  也无需 @ComponentScan 扫描

