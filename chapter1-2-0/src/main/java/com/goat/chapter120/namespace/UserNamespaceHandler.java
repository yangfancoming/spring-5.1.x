package com.goat.chapter120.namespace;


import com.goat.chapter120.parser.UserBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


public class UserNamespaceHandler extends NamespaceHandlerSupport {

	// 这里注册的 "user" 必须与customer-tag.xml 文件中 springtag:user 标签后的apple保持一致，否则将找不到相应的处理逻辑
	@Override
    public void init() {
        registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
    }
}
