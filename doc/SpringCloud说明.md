# SpringCloud

## 什么是SpringCloud?

分布式微服务架构的一站式解决方案，是多种微服务架构落地技术的集合体，俗称微服务全家桶

SpringCloud是一篮子方案，所fa有的东西都给你包起来。

## 微服务的说明，包含哪些：

![image-20211006204840747](assets\image-20211006204840747.png)

## 谈谈你对分布式架构的了解？

分布式架构的系统至少包含以下内容：

![image-20211006205045323](assets\image-20211006205045323.png)



![image-20211006205549466](assets\image-20211006205549466.png)



### 大厂微服务架构的实现

#### 京东：

![image-20211006210017110](assets\image-20211006210017110.png)



#### 阿里：

![image-20211006210209488](assets\image-20211006210209488.png)



#### 京东物流:

![image-20211006210313376](assets\image-20211006210313376.png)



#### 总结：跟业务无关的剥离出去，跟业务相关的沉下来。

![image-20211006210440726](assets\image-20211006210440726.png)



### 通用的微服务架构：

![image-20211006211119756](assets\image-20211006211119756.png)



## 天上飞的理念必然有落地的实现

### 2020.2月以前主流的实现。

![image-20211006211420773](assets\image-20211006211420773.png)



# 一、SpringBoot2.X版和SpringCloud H版

## 1、版本选型

![image-20211006222412326](assets\image-20211006222412326.png)

## 2、各组件说明

![image-20211006222555984](assets\image-20211006222555984.png)



## 3、订单-支付微服务

约定 > 配置 > 编码

### 工程说明：

#### 1）父工程

**父工程pom文件说明**：

<dependencyManagement>一般用于父工程，子模块继承以后，提供作用：锁定版本+子module不用写groupId和version

它只是声明依赖，<b>并不实现引入</b>，因此子项目需要显示的声明需要用到的依赖。

如果不在子项目中声明依赖，是不会从父项目中继承下来的；只有在子项目中写了该依赖项，并且没有指定具体版本，才会从父项目中继承该项，并且version和scope都读取自父pom;

如果子项目指定了版本号，那么会使用子项目中指定的jar版本。

#### 2）支付模块8001

![image-20211007132149303](assets\image-20211007132149303.png)

#### 3）消费模块80

![image-20211007132220227](assets\image-20211007132220227.png)

消费模块与支付模块之间的RESTful调用采用 RestTemplate

```java
@Configuration
public class ApplicationContextConfig {
    @Bean
    //@LoadBalanced //如果自己写了负载均衡算法，要把Ribbon的LoadBalanced注解注释掉
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
@GetMapping("/consumer/payment/create")
public CommonResult<Payment> create(Payment payment){
    return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
}

@GetMapping("/consumer/payment/get/{id}")
public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
    return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
}
```





#### 4）工程重构

将复用代码添加进公共模块

![image-20211007132307973](assets\image-20211007132307973.png)

#### 5）Eureka Server端服务注册中心（单机版）

![image-20211007140751736](assets\image-20211007140751736.png)

①pom.xml

```xml
<!--eureka-server-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

②主启动类标明是eureka server端

```java
@SpringBootApplication
@EnableEurekaServer  /**表明服务注册中心**/
public class EurekaMain7001 {
    public static void main(String[] args){
        SpringApplication.run(EurekaMain7001.class,args);
    }
}
```

③启动后访问http://localhost:7001/

![image-20211007141626517](assets\image-20211007141626517.png)



#### 6）支付模块8001注册进Eureka

该模块为Eureka的Client端，修改支付模块8001

①pom.xml，添加client端

```xml
<!--eureka-client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

②修改yml

```yaml
eureka:
  client:
    register-with-eureka: true  #表示是否将自己注册进EurekaServer默认为true
    fetchRegistry: true  #是否从EurekaServer抓取已有的注册信息，默认为true,单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    service-url:
      #入住地址即Eureka的服务器端地址
      defaultZone: http://localhost:7001/eureka
      #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群
  instance:
    instance-id: payment8001
    prefer-ip-address: true
```

③主启动类，添加@EnableEurekaClient

```java
@SpringBootApplication
@EnableEurekaClient
//@EnableDiscoveryClient
public class PaymentMain8001 {
    public static void main(String[] args){
        SpringApplication.run(PaymentMain8001.class,args);
    }
}
```

④访问http://localhost:7001/

红色提示字是 Eureka的自我保护机制

![image-20211007143303893](assets\image-20211007143303893.png)

![image-20211007143352572](assets\image-20211007143352572.png)



#### 7）消费模块80注册进Eureka

①pom.xml 添加Eureka Client端

```xml
<!--eureka-client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

②修改yml

```yaml
server:
  port: 80

spring:
  application:
    name: cloud-order-service

eureka:
  client:
    register-with-eureka: true  #表示是否将自己注册进RurekaServer默认为true
    fetchRegistry: true  #是否从RurekaServer抓取已有的注册信息，默认为true,单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    service-url:
      defaultZone: http://localhost:7001/eureka
      #defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群
```

③主启动类

```java
@SpringBootApplication
@EnableEurekaClient
public class OrderMain80 {
    public static void main(String[] args){
        SpringApplication.run(OrderMain80.class,args);
    }
}
```

④访问http://localhost:7001/

![image-20211007144039714](assets\image-20211007144039714.png)

#### 8）Eureka Server端服务注册中心（集群版）

RPC远程服务调用的核心：**高可用**

本质：相互注册，互相守望

添加7002工程

![image-20211007150203176](assets\image-20211007150203176.png)

①修改hosts配置文件，C:\Windows\System32\drivers\etc\hosts

```properties
#springcloud
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com
```

②修改7001和7002工程的yml，相互注册

7001:

```yaml
server:
  port: 7001
eureka:
  instance:
    hostname: eureka7001.com #eureka服务端的实例名称
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone:  http://eureka7002.com:7002/eureka/
      #单机就是7001自己
      #defaultZone:  http://${eureka.instance.hostname}:${server.port}/eureka/
```

7002:

```yaml
server:
  port: 7002
eureka:
  instance:
    hostname: eureka7002.com #eureka服务端的实例名称
  client:
    #false表示不向注册中心注册自己
    register-with-eureka: false
    #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    fetch-registry: false
    service-url:
      #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone:  http://eureka7001.com:7001/eureka/
```

③主启动

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaMain7002 {
    public static void main(String[] args){
        SpringApplication.run(EurekaMain7002.class,args);
    }
}
```

④因为已经进行域名注册，访问http://eureka7001.com:7001/  http://eureka7002.com:7002/

![image-20211007150943816](assets\image-20211007150943816.png)

![image-20211007151009820](assets\image-20211007151009820.png)

#### 9）将支付模块8001、消费模块80同时注册进Eureka Server

修改各个模块的yml即可

8001:

```yaml
eureka:
  client:
    register-with-eureka: true  #表示是否将自己注册进EurekaServer默认为true
    fetchRegistry: true  #是否从EurekaServer抓取已有的注册信息，默认为true,单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    service-url:
      #入住地址即Eureka的服务器端地址
      #defaultZone: http://localhost:7001/eureka
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群
```

80:

```yaml
eureka:
  client:
    register-with-eureka: false  #表示是否将自己注册进RurekaServer默认为true
    fetchRegistry: true  #是否从RurekaServer抓取已有的注册信息，默认为true,单节点无所谓，集群必须设置为true才能配合ribbon使用负载均衡
    service-url:
      #defaultZone: http://localhost:7001/eureka
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka #集群
```



#### 10）支付模块集群

添加支付模块8002

![image-20211007152128931](assets\image-20211007152128931.png)

配置同支付模块8001

访问可看见

![image-20211007152924154](assets\image-20211007152924154.png)

修改消费模块80访问地址

```java
//private static final String PAYMENT_URL = "http://localhost:8001";//单点
public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";//集群，微服务名称
@Resource
private RestTemplate restTemplate;

@GetMapping("/consumer/payment/create")
public CommonResult<Payment> create(Payment payment){
    return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,CommonResult.class);
}

@GetMapping("/consumer/payment/get/{id}")
public CommonResult<Payment> getPayment(@PathVariable("id") Long id){
    return restTemplate.getForObject(PAYMENT_URL+"/payment/get/"+id,CommonResult.class);
}
```

查询：

![image-20211007153703046](assets\image-20211007153703046.png)

报错是因为找到的当前微服务名称下可能有多个微服务，但是并不知道是哪个具体微服务提供服务。此时需要开启负载均衡！！

**使用@LoadBalanced注解，开启负载均衡的能力**，默认是轮询的方式

```java
@Configuration
public class ApplicationContextConfig {
    @Bean
    @LoadBalanced //如果自己写了负载均衡算法，要把Ribbon的LoadBalanced注解注释掉
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

现整个工程结构：服务注册中心和支付模块都为集群模式

![image-20211007154421530](assets\image-20211007154421530.png)



#### 11）服务发现

将服务的信息暴露给调用方

①引入DiscoveryClient

```java
/**
  * 对于注册进eureka里面的微服务，可以通过服务发现来获得该服务的信息
  */
@Resource
private DiscoveryClient discoveryClient;
```

②编写需要获取哪些具体信息的代码

```java
@GetMapping(value = "/payment/discovery")
public Object discoveryServices(){
    //查看eureka上注册了多少服务，服务列表清单
    List<String> services = discoveryClient.getServices();
    for (String element : services){
        log.info("***************** service:"+element);
    }

    //查看某个服务的具体信息，通过微服务的名称获取具体信息
    List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
    for (ServiceInstance instance : instances){
        log.info("scheme:"+instance.getScheme()+" serviceId : "+instance.getServiceId()+" host: "+instance.getHost()+" uri: "+instance.getUri()+" port : "+instance.getPort());
    }
    return this.discoveryClient;
}
```

③主启动类添加服务发现注解@EnableDiscoveryClient

```java
@SpringBootApplication
@EnableEurekaClient
//服务发现
@EnableDiscoveryClient
public class PaymentMain8001 {
    public static void main(String[] args){
        SpringApplication.run(PaymentMain8001.class,args);
    }
}
```

访问打印内容：

```markdown
***************** service:cloud-payment-service
***************** service:cloud-order-service
scheme:http instanceId : payment8002 serviceId : CLOUD-PAYMENT-SERVICE host: 192.168.116.1 uri: http://192.168.116.1:8002 port : 8002
scheme:http instanceId : payment8001 serviceId : CLOUD-PAYMENT-SERVICE host: 192.168.116.1 uri: http://192.168.116.1:8001 port : 8001
```



#### 12）支付模块8004注册进zookeeper

![image-20211007194321362](assets\image-20211007194321362.png)

①pom.xml

```xml
<!-- SpringBoot整合zookeeper客户端 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    <!--先排除自带的zookeeper3.5.3-->
    <exclusions>
        <exclusion>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<!--添加zookeeper3.4.11版本-->
<dependency>
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.11</version>
</dependency>
```

②yml

```yaml
#服务别名----注册zookeeper到注册中心名称
spring:
  application:
    name: cloud-provider-payment
  cloud:
    zookeeper:
      connect-string: 192.168.116.128:2181
```



③主启动类

```java
@SpringBootApplication
@EnableDiscoveryClient //该注解用于使用consul或者zookeeper作为注册中心注册服务
public class PaymentMain8004 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8004.class,args);
    }
}
```



zookeeper注册的结点为**临时结点**，如果将服务停了，一会后结点消失。

![image-20211007200647770](assets\image-20211007200647770.png)

再次将服务启动起来，临时结点又回来了。

![image-20211007201013161](assets\image-20211007201013161.png)

获取结点信息：

![image-20211007201251285](assets\image-20211007201251285.png)

```json
{
  "name": "cloud-provider-payment",
  "id": "c1083ba7-a792-4d1f-9eaf-74dbe1b3f3ab",
  "address": "DESKTOP-FCMFQP9",
  "port": 8004,
  "sslPort": null,
  "payload": {
    "@class": "org.springframework.cloud.zookeeper.discovery.ZookeeperInstance",
    "id": "application-1",
    "name": "cloud-provider-payment",
    "metadata": {
      "instance_status": "UP"
    }
  },
  "registrationTimeUTC": 1633608549078,
  "serviceType": "DYNAMIC",
  "uriSpec": {
    "parts": [
      {
        "value": "scheme",
        "variable": true
      },
      {
        "value": "://",
        "variable": false
      },
      {
        "value": "address",
        "variable": true
      },
      {
        "value": ":",
        "variable": false
      },
      {
        "value": "port",
        "variable": true
      }
    ]
  }
}
```



13）消费模块80注册进zookeeper

![image-20211007203427792](assets\image-20211007203427792.png)

配置同支付模块8004

启动80，8004和80都注册进zookeeper

![image-20211007204040721](assets\image-20211007204040721.png)

页面测试：

![image-20211007204220595](assets\image-20211007204220595.png)



#### 13）支付模块8006注册进consul

![image-20211007210950271](assets\image-20211007210950271.png)

启动8006

![image-20211007211245585](assets\image-20211007211245585.png)



#### 14）消费模块80注册进consul

![image-20211007211725677](assets\image-20211007211725677.png)

启动80

![image-20211007211941303](assets\image-20211007211941303.png)

![image-20211007212108067](assets\image-20211007212108067.png)



#### 15）修改消费模块80的负载均衡Ribbon

①添加自定义的负载均衡类

![image-20211007222527335](assets\image-20211007222527335.png)

②MySelfRule类

```java
@Configuration
public class MySelfRule {
    @Bean
    public IRule myRule(){
        //定义为随机
        return new RandomRule();
    }
}
```

③主启动类添加@RibbonClient，访问CLOUD-PAYMENT-SERVICE微服务，采用自定义的负载均衡方法

```java
@SpringBootApplication
@EnableEurekaClient
@RibbonClient(name = "CLOUD-PAYMENT-SERVICE",configuration = MySelfRule.class)
public class OrderMain80 {
    public static void main(String[] args){
        SpringApplication.run(OrderMain80.class,args);
    }
}
```

 





## 4、热部署

见 <b>思维导图 第四章 --> 4.微服务架构编码构建 --> Rest微服务工程构建</b>  

## 5、Eureka(已停更)

![image-20211007140417671](assets\image-20211007140417671.png)





# 二、SpringCloud Alibaba