package com.goat.spring.tx;


import com.goat.spring.tx.config.MainConfig;
import com.goat.spring.tx.service.PayService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class MainClass {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfig.class);
        PayService payService = context.getBean(PayService.class);
        payService.pay("1",10);


    }
}
