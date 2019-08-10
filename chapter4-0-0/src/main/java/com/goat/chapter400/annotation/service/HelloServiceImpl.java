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

	/** 测试 正常方法*/
	@Override
    public String sayHiService1(String str){
        System.out.println("目标方法执行----------sayHiService1" + str);
        return "return sayHiService1";
    }

    /** 测试 异常方法*/
	@Override
    public String sayHiService2(){
		System.out.println("目标方法执行----------sayHiService2");
       if (true){
           throw  new RuntimeException("123");
       }
		return "return sayHiService2";
    }
}
