package com.how2java.tmall.dao;

import java.util.List;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductImageDAO extends JpaRepository<ProductImage, Integer>{

    public List<ProductImage> findByProductAndTypeOrderByIdDesc(Product product, String type);

}

