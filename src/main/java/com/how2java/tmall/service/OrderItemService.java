package com.how2java.tmall.service;

import com.how2java.tmall.dao.OrderItemDAO;
import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;
import com.how2java.tmall.pojo.Product;
import org.aspectj.weaver.ast.Or;
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

    public void add(OrderItem bean){
        orderItemDAO.save(bean);
    }

    public void delete(int id) {
        orderItemDAO.delete(id);
    }

    public OrderItem get(int id) {
        return orderItemDAO.findOne(id);
    }

    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    public int getSaleCount(Product product) {
        List<OrderItem> orderItems = listByProduct(product);
        int result = 0;
        for(OrderItem orderItem: orderItems) {
            if(orderItem.getOrder() != null && orderItem.getOrder().getPayDate() != null) {
                result += orderItem.getNumber();
            }
        }
        return result;
    }

}
