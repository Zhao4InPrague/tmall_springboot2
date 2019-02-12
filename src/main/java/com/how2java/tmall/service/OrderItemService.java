package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);

        float total = 0;
        int totalNumber = 0;

        for(OrderItem orderItem: orderItems) {
            total = orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
            totalNumber += orderItem.getNumber();
            productImageService.setFirstProductImage(orderItem.getProduct());
        }

        order.setTotal(total);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);

    }

    public void fill(List<Order> orders) {
        for(Order order: orders) {
            fill(order);
        }
    }


}
