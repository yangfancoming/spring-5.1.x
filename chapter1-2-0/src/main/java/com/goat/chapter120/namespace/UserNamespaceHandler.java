package com.goat.chapter120.namespace;


import com.goat.chapter120.parser.UserBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


public class UserNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("user",new UserBeanDefinitionParser());
    }
}
