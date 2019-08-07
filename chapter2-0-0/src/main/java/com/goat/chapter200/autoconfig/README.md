# 自动配置总结：
     其实就是 组合注解配置  @Component  @Autowired  @ComponentScan 

     *  不管是构造器、 Setter方法还是其他的方法， Spring都会尝试满足方法参数上所声明的依赖。
     *  假如有且只有一个bean匹配依赖需求的话， 那么这个bean将会被装配进来。
     *
     *  如果没有匹配的bean， 那么在应用上下文创建的时候， Spring会抛出一个异常。
     *  为了避免异常的出现， 你可以将@Autowired的required属性设置为false （默认为true）
     *
     *  如果有多个bean都能满足依赖关系的话， Spring将会抛出一个异常，表明没有明确指定要选择哪个bean进行自动装配。
     
     *  在第3章中， 我们 会进一步讨论自动装配中的歧义性