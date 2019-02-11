package com.how2java.tmall.service;

import com.how2java.tmall.dao.PropertyDAO;
import com.how2java.tmall.dao.PropertyValueDAO;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.pojo.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PropertyValueService {

    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    public void update(PropertyValue bean){
        propertyValueDAO.save(bean);
    }

    public PropertyValue getByProductAndProperty(Product product, Property property) {
        return propertyValueDAO.getByProductAndProperty(product, property);
    }

    public List<PropertyValue> list(Product product){
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

    public void init(Product product){
        //这个在contoller里会init
        List<Property> properties = propertyService.list(product.getCategory());

        for(Property property:properties) {
            PropertyValue propertyValue = propertyValueDAO.getByProductAndProperty(product, property);
            if(propertyValue == null) {
                PropertyValue bean = new PropertyValue();
                bean.setProperty(property);
                bean.setProduct(product);
                propertyValueDAO.save(bean);
            }
        }

    }

}
