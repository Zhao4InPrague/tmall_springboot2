package com.how2java.tmall.service;

import com.how2java.tmall.dao.ReviewDAO;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    ReviewDAO reviewDAO;
    @Autowired
    ProductService productService;

    public void add(Review bean) {
        reviewDAO.save(bean);
    }

    public int getCount(Product product) {
        return reviewDAO.countByProduct(product);
    }

    public List<Review> list(Product product) {
        return reviewDAO.findByProductOrderByIdDesc(product);
    }

}
