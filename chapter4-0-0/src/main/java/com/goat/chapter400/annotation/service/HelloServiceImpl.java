package com.goat.chapter400.annotation.service;

import org.springframework.stereotype.Service;

/**
 * Created by 64274 on 2018/9/26.
 *
 * @author 山羊来了
 * @Description: TODO
 * @date 2018/9/26---15:32
 */
@Service
public class HelloServiceImpl implements HelloService {

	@Override
    public String sayHiService1(){
        System.out.println("sayHiService1 方法执行！"); // doit http://localhost:8341/hello1 请求  为啥执行不到这里？
        return "HelloService...........1";
    }

	@Override
    public String sayHiService2(){
        System.out.println("sayHiService2 方法执行！");
       if (true){
           throw  new RuntimeException("123");
       }
        return "HelloService...........2";
    }
}
