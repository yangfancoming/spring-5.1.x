package com.goat.spring.tx.service;


public interface PayService {


    void pay(String accountId, double money);

    void updateProductStore(Integer productId);
}
