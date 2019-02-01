package com.how2java.tmall.web;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;

//    @GetMapping("/category")
//    public List<Category> list() throws Exception {
//        return categoryService.list();
//    }

    @GetMapping("/category")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size)
            throws Exception {
        start = start < 0? 0: start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

}
