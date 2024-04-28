package com.tansci.domain.picture.pojo;

import lombok.Data;

@Data
public class OrderItem {
        private  String orderItemId;
        private  String orderId;
        private  String productName;
        private  String brandName;
        private  String sellPrice;

}
