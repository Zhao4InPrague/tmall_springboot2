package com.how2java.tmall.comparator;

import com.how2java.tmall.pojo.Product;

import java.util.Comparator;

public class ProductAllComparator implements Comparator<Product>{

    //把 销量x评价 高的放前面,这个比法太屌丝了
    @Override
    public int compare(Product o1, Product o2) {
        return o2.getReviewCount()*o2.getSaleCount() - o1.getReviewCount()*o1.getSaleCount();
    }
}
