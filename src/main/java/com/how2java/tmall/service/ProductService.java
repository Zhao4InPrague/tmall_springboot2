package com.how2java.tmall.service;

import com.how2java.tmall.dao.ProductDAO;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.util.Page4Navigator;
import com.how2java.tmall.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "products")
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

    @CacheEvict(allEntries = true)
    public void add(Product product){

        productDAO.save(product);

    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        productDAO.delete(id);
    }

    @Cacheable(key = "'products-one-'+ #p0")
    public Product get(int id) {
        return productDAO.findOne(id);
    }

    @CacheEvict(allEntries = true)
    public void update(Product product) {
        productDAO.save(product);
    }

    @Cacheable(key = "'products-cid-'+#p0+'-page-'+#p1 + '-' + #p2 ")
    public Page4Navigator<Product> list(int cid, int start, int size, int navigatePages){

        Category category = categoryService.get(cid);

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page<Product> PageFromJPA = productDAO.findByCategory(category, pageable);
        return new Page4Navigator<>(PageFromJPA, navigatePages);

    }

    @Cacheable(key = "'products-cid-'+ #p0.id")
    public List<Product> listByCategory(Category category) {
        return productDAO.findByCategoryOrderById(category);
    }

    public void fill(Category category){
        //如果在ProductService的一个方法里，调用另一个 缓存管理 的方法，不能够直接调用，需要通过一个工具，再拿一次 ProductService， 然后再调用。
        ProductService productService = SpringContextUtil.getBean(ProductService.class);
        List<Product> products = productService.listByCategory(category);

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
        //写了sort和pageable缺不用page4，没有很懂本来以为这些是一套的...现在很尴尬。。不是很清楚这种写法
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        List<Product> products = productDAO.findByNameLike("%"+keyword+"%",pageable);
        return products;
    }

}
