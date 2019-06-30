
package com.goat.spring.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class MessagePrinter {

	@Autowired
    final private MessageService service;

    public MessagePrinter(MessageService service) {
        this.service = service;
    }

    public void printMessage() {
        System.out.println(service.getMessage());
    }
}