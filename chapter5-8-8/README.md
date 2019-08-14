#总结
    1.各浏览器默认的语言各不相同，可通过设置进行修改调整；
    2.客户端请求头Accept-Languages的第一个（权重最大）为Spring MVC使用的Locale；
    3.权重最大的Locale名必须与属性文件一致，否则找不到，如Accept-Languages的第一个为en，
        则资源文件名必须设置为messages_en.properties，如果第一个是zh-CN，则资源名称为messages_zh _CN.properties；
    4.使用messageSource Bean时，如果资源文件放在类路径下，basename的值必须以classpath:开头。