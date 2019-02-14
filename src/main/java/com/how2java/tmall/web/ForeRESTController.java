package com.how2java.tmall.web;

import com.how2java.tmall.pojo.*;

import com.how2java.tmall.comparator.*;
import com.how2java.tmall.service.*;
import com.how2java.tmall.util.Result;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ForeRESTController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    public Object home(){
        List<Category> categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

//    @PostMapping("/foreregister")
//    public Object register(@RequestBody User user) {
//        String name = user.getName();
//        String password = user.getPassword();
//        name = HtmlUtils.htmlEscape(name);
//        user.setName(name);
//        boolean exist = userService.isExist(name);
//        if(exist) {
//            String message = "用户名已经被使用,不能使用";
//            return Result.fail(message);
//        }
//
//        user.setPassword(password);
//        userService.add(user);
//
//        return Result.success();
//    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);

        boolean exist = userService.isExist(name);

        if(exist) {
            String message = "用户名已经被使用,不能使用";
            return Result.fail(message);
        }

        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";

        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);

        userService.add(user);

        return Result.success();
    }

//    @PostMapping("/forelogin")
//    public Object login(@RequestBody User userParam, HttpSession httpSession) {
//
//        String name = userParam.getName();
//        String password = userParam.getPassword();
//
//        User user = userService.get(name, password);
//        if(null == user) {
//            String message = "账号密码错误";
//            return Result.fail(message);
//        }else {
//            httpSession.setAttribute("user", user);
//            return Result.success();
//        }
//
//    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);
        String password = userParam.getPassword();

        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken(name, password);

        try {
            subject.login(token);
            User user = userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException e) {
            String message ="账号密码错误";
            return Result.fail(message);
        }
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductDetailImages(productDetailImages);
        product.setProductSingleImages(productSingleImages);

        List<PropertyValue> propertyValues = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProductImage(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", propertyValues);
        map.put("reviews", reviews);

        return Result.success(map);

    }

//    @GetMapping("forecheckLogin")
//    public Object checkLogin(HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        if(null != user) {
//            return Result.success();
//        }else {
//            return Result.fail("no login");
//        }
//    }
    @GetMapping("forecheckLogin")
    public Object checkLogin() {
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()) {
            return Result.success();
        } else {
            return Result.fail("no login");
        }
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable("cid") int cid, String sort) {
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);

        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(category.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(category.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(category.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(category.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(category.getProducts(),new ProductAllComparator());
                    break;
            }
        }

        return category;
    }

    @PostMapping("foresearch")
    public Object search(String keyword) {
        if(null == keyword) {
            keyword = "";
        }

        List<Product> products = productService.search(keyword, 0, 20);
        productImageService.setFirstProductImages(products);
        productService.setSaleAndReviewNumber(products);
        return products;
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        Product product = productService.get(pid);
        int ooid = 0;
        User user = (User) session.getAttribute("user");
        boolean found = false;
        List<OrderItem> orderItems = orderItemService.listByUser(user);

        for(OrderItem orderItem: orderItems) {
            if(orderItem.getProduct().getId() == product.getId()) {
                orderItem.setNumber(orderItem.getNumber()+num);
                orderItemService.update(orderItem);
                found = true;
                ooid = orderItem.getId();
                break;
            }
        }

        if(!found) {
            OrderItem orderItem = new OrderItem();
            orderItem.setUser(user);
            orderItem.setProduct(product);
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            ooid = orderItem.getId();
        }

        return ooid;
    }

    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session){

        return buyoneAndAddCart(pid, num, session);

    }

    @GetMapping("forebuy")
    public Object buy(String[] oiid, HttpSession session) {

        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for(String strid: oiid) {
            int id = Integer.parseInt(strid);
            OrderItem orderItem = orderItemService.get(id);
            total += orderItem.getProduct().getPromotePrice()*orderItem.getNumber();
            orderItems.add(orderItem);
        }

        productImageService.setFirstProdutImagesOnOrderItems(orderItems);

        session.setAttribute("ois", orderItems);
        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);

        return Result.success(map);

    }

    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> orderItems = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        return orderItems;
    }

    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session, int pid, int num) {
        User user = (User) session.getAttribute("user");
        if(null == user) {
            return Result.fail("no login");
        }

        List<OrderItem> orderItems = orderItemService.listByUser(user);
        for (OrderItem orderItem: orderItems) {
            if(orderItem.getProduct().getId() == pid) {
                orderItem.setNumber(num);
                orderItemService.update(orderItem);
                break;
            }
        }
        return Result.success();
    }

    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if(null == user){
            return Result.fail("no login");
        }
        orderItemService.delete(oiid);
        return Result.success();
    }

    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(null == user) {
            return Result.fail("no login");
        }
        //??根据当前时间加上一个4位随机数生成订单号
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        order.setStatus(OrderService.waitPay);
        List<OrderItem> orderItems = (List<OrderItem>) session.getAttribute("ois");

        float total = orderService.add(order, orderItems);

        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }

    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        return order;
    }

    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if(null == user) {
            return Result.fail("no login");
        }
        List<Order> orders = orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(orders);
        return orders;
    }

    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.cacl(order);
        orderService.removeOrderFromOrderItem(order);
        return order;
    }

    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitReview);
        order.setConfirmDate(new Date());
        orderService.update(order);
        return Result.success();
    }

    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.delete);
        orderService.update(order);
        return Result.success();
    }

    @GetMapping("forereview")
    public Object review(int oid){
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        orderService.removeOrderFromOrderItem(order);
        Product product = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        Map<String, Object> map = new HashMap<>();
        map.put("p", product);
        map.put("o", order);
        map.put("review", reviews);

        return Result.success(map);
    }

    @PostMapping("foredoreview")
    public Object doreview(HttpSession session, int oid, int pid, String content){
        Order order = orderService.get(oid);
        order.setStatus(OrderService.finish);
        orderService.update(order);

        Product product = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setUser(user);
        review.setProduct(product);
        review.setCreateDate(new Date());
        reviewService.add(review);
        return Result.success();
    }

}
