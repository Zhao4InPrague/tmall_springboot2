package com.how2java.tmall.dao;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyDAO extends JpaRepository<Property, Integer>{
    //原始没有的dao就没办法了必须这样写
    //Page4Navigator没法用，里面Pageable也没发用
    Page<Property> findByCategory(Category category, Pageable pageable);

}
