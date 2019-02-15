package com.how2java.tmall;

import com.how2java.tmall.util.PortUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableCaching
@EnableElasticsearchRepositories(basePackages = "com.how2java.tmall.es")
@EnableJpaRepositories(basePackages = {"com.how2java.tmall.dao", "com.how2java.tmall.pojo"})
//在 Application 中，为es和jpa分别指定不同的包名，否则会出错
//因为 jpa 的dao 做了 链接 redis 的，如果放在同一个包下，会彼此影响，出现启动异常。
//不是放在service上了吗咋又和dao扯上了
public class Application {

    static {
        PortUtil.checkPort(6379,"Redis 服务端",true);
        PortUtil.checkPort(9300,"ElasticSearch 服务端",true);
        PortUtil.checkPort(5601,"Kibana 工具", true);
    }

    public static void main(String[] args){

        SpringApplication.run(Application.class, args);

    }

}
