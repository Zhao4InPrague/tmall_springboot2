package com.how2java.tmall.web;

import com.how2java.tmall.dao.PropertyDAO;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page4Navigator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class PropertyController {

    @Autowired
    PropertyService propertyService;

    @GetMapping("properties/{id}")
    public Property get(@PathVariable("id") int id) throws Exception {
        return propertyService.get(id);
    }

    @PostMapping("/properties")
    public Object add(@RequestBody Property bean) throws Exception {
        propertyService.add(bean);
        // return bean??
        return bean;
    }

    @DeleteMapping("/properties/{id}")
    public String delete(@PathVariable("id") int id) throws Exception {
        propertyService.delete(id);
        return null;
    }

    @PutMapping("/properties")
    public Object update(@RequestBody Property bean) throws Exception {
        //咋有时候写@RequestBody，有时候写啊
        //category里传了id和这里还不一样
        propertyService.update(bean);
        return bean;
    }

    @GetMapping("/categories/{cid}/properties")
    public Page4Navigator<Property> list(@PathVariable("cid") int cid, @RequestParam(value = "start", defaultValue = "0") int start, @RequestParam(value = "size", defaultValue = "5") int size)
            throws Exception {
        start = start<0? 0: start;
        Page4Navigator<Property> page = propertyService.list(cid, start, size, 5);
        return page;
    }


}
