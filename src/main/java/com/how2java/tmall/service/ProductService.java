package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductDAO productDAO;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    ReviewService reviewService;

    public void add(Product product){

        productDAO.save(product);

    }

    public void delete(int id) {
        productDAO.delete(id);
    }

    public Product get(int id) {
        return productDAO.findOne(id);
    }

    public void update(Product product) {
        productDAO.save(product);
    }

    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages){

        Category category = categoryService.get(cid);

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> PageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(PageFromJPA, navigatePages);

    }

    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    public void fill(Category category){

        List<Product> products = listByCategory(category);
        //为啥在这里又吧image放这里
        productImageService.setFirstProductImages(products);
        category.setProducts(products);

    }

    public void fill(List<Category> categories) {
        for (Category category: categories) {
            fill(category);
        }
    }

    public void fillByRow(List<Category> categories) {
        int row = 8;
        for(Category category: categories) {
            List<Product> products = category.getProducts();
            List<List<Product>> productsByRow = new ArrayList<>();
            for(int i = 0; i < products.size(); i+=row) {
                int size = i + row;
                size = size > products.size()? products.size(): size;
                List<Product> productsOfEachRow = products.subList(i, size);
                productsByRow.add(productsOfEachRow);
            }
            category.setProductsByRow(productsByRow);
        }
    }

    public void setSaleAndReviewNumber(Product product) {
        int saleCount = orderItemService.getSaleCount(product);
        product.setSaleCount(saleCount);

        int reviewCount = reviewService.getCount(product);
        product.setReviewCount(reviewCount);
    }

    public void setSaleAndReviewNumber(List<Product> products) {
        for(Product product: products) {
            setSaleAndReviewNumber(product);
        }
    }

    public List<Product> search(String keyword, int start, int size){
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products = productDAO.findByNameLike("%"+keyword+"%",pageable);
        return products;
    }

}
