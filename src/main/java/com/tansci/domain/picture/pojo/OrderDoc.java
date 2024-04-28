package com.tansci.domain.picture.pojo;

import lombok.Data;

import java.util.List;

@Data
public class OrderDoc {
    private  String orderId;
    private  String orderNo;
    private  String orderUserName;
    private List<OrderItem> orderItems;


}

