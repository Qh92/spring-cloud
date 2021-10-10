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



## 3、订单-生产微服务

约定 > 配置 > 编码

### 服务注册：

#### Eureka、Zookeeper、Consul、Nacos

### 服务调用：

#### Ribbon、LoadBalancer、OpenFeign

### 服务降级/熔断：

#### Hystrix、resilience4j、sentinel





### 工程说明：

#### 1）父工程

**父工程pom文件说明**：

<dependencyManagement>一般用于父工程，子模块继承以后，提供作用：锁定版本+子module不用写groupId和version

它只是声明依赖，<b>并不实现引入</b>，因此子项目需要显示的声明需要用到的依赖。

如果不在子项目中声明依赖，是不会从父项目中继承下来的；只有在子项目中写了该依赖项，并且没有指定具体版本，才会从父项目中继承该项，并且version和scope都读取自父pom;

如果子项目指定了版本号，那么会使用子项目中指定的jar版本。

#### 2）生产模块8001

![image-20211007132149303](assets\image-20211007132149303.png)

#### 3）消费模块80

![image-20211007132220227](assets\image-20211007132220227.png)

消费模块与生产模块之间的RESTful调用采用 Ribbon + RestTemplate

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

#### **==服务注册Eureka==**

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



#### 6）生产模块8001注册进Eureka

该模块为Eureka的Client端，修改生产模块8001

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

#### 9）将生产模块8001、消费模块80同时注册进Eureka Server

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



#### 10）生产模块集群

添加生产模块8002

![image-20211007152128931](assets\image-20211007152128931.png)

配置同生产模块8001

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

现整个工程结构：服务注册中心和生产模块都为集群模式

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



#### **==服务注册Zookeeper==**

#### 12）生产模块8004注册进zookeeper

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

配置同生产模块8004

启动80，8004和80都注册进zookeeper

![image-20211007204040721](assets\image-20211007204040721.png)

页面测试：

![image-20211007204220595](assets\image-20211007204220595.png)



#### **==服务注册Consul==**

#### 13）生产模块8006注册进consul

![image-20211007210950271](assets\image-20211007210950271.png)

启动8006

![image-20211007211245585](assets\image-20211007211245585.png)



#### 14）消费模块80注册进consul

![image-20211007211725677](assets\image-20211007211725677.png)

启动80

![image-20211007211941303](assets\image-20211007211941303.png)

![image-20211007212108067](assets\image-20211007212108067.png)



#### **==服务调用Ribbon==**

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

 

#### 16）自定义一个轮询算法

①修改生产模块8001、8002 的controller

```java
@GetMapping(value = "/payment/lb")
public String getPaymentLB(){
    return serverPort;
};
```

②消费模块80 配置类去掉@LoadBalanced注解

```java
@Configuration
public class ApplicationContextConfig {
    @Bean
    //@LoadBalanced //如果自己写了负载均衡算法，要把Ribbon的LoadBalanced注解注释掉
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
```

③消费模块80添加自定义的负载均衡算法

```java
public interface LoadBalancer {
    ServiceInstance instance(List<ServiceInstance> serviceInstances);
}
@Component
public class MyLB implements LoadBalancer{
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    public final int getAndIncrement(){
        int current;
        int next;
        do{
            //得到当前值
            current = this.atomicInteger.get();
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
        }while(!this.atomicInteger.compareAndSet(current,next));
        System.out.println("******************第几次访问:次数next:"+next);
        return next;
    }
    @Override
    public ServiceInstance instance(List<ServiceInstance> serviceInstances) {
        int index = getAndIncrement() % serviceInstances.size();
        return serviceInstances.get(index);
    }
}
```

④消费模块80controller添加方法

```java
@Resource
private LoadBalancer loadBalancer;
@Resource
private DiscoveryClient discoveryClient;

@GetMapping(value = "/consumer/payment/lb")
public String getPaymentLB(){
    List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
    if(instances == null || instances.size() <= 0){
        return null;
    }
    ServiceInstance serviceInstance = loadBalancer.instance(instances);
    URI uri = serviceInstance.getUri();
    return restTemplate.getForObject(uri+"/payment/lb",String.class);
}
```



#### **==服务调用openFeign==**

#### 17）消费模块80采用openFeign服务调用

##### openFeign客户端工程

![image-20211008194639668](assets\image-20211008194639668.png)

①pom.xml引入openfeign，openfeign自带ribbon负载均衡能力

```xml
<!--openfeign-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

②主启动类添加注解@EnableFeignClients

```java
@SpringBootApplication
@EnableFeignClients
public class OrderFeignMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderFeignMain80.class,args);
    }
}
```

③调用方添加openfeign接口

```java
@Component
//微服务名称，找哪个具体的微服务
@FeignClient(value = "CLOUD-PAYMENT-SERVICE")
public interface PaymentFeignService {
    /**
     * 调用对应的微服务的对外暴露的方法
     */
    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById (@PathVariable("id") Long id);

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout();
}
```



##### 服务提供方超时openfeign策略

openfeign客户端默认只等待一秒，如果服务端处理需要超过1s，导致feign客户端不想等待了，直接返回报错

![image-20211008200228449](assets\image-20211008200228449.png)

解决办法，openfeign客户端设置超时时间

```yaml
#设置feign客户端超时时间(openFeign默认支持ribbon)
ribbon:
  #指的是建立连接所用的时间，适用于网络状况正常的情况下，两端连接所用的时间
  ReadTimeout: 5000
  #建立连接后从服务器读取到可用资源所用时间
  ConnectTimeout: 5000
```

![image-20211008200540350](assets\image-20211008200540350.png)



##### openfeign日志打印功能

①编写日志打印级别

```java
@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }
}
```

![image-20211008201327141](assets\image-20211008201327141.png)

②yml配置中配置

```yaml
logging:
  level:
    #feign日志以什么级别监控哪个接口
    com.qh.springcloud.service.PaymentFeignService: debug
```

调用日志：

```xml
: 调用服务开始
: [PaymentFeignService#getPaymentById] ---> GET http://CLOUD-PAYMENT-SERVICE/payment/get/1 HTTP/1.1
: [PaymentFeignService#getPaymentById] ---> END HTTP (0-byte body)
: [PaymentFeignService#getPaymentById] <--- HTTP/1.1 200 (5ms)
: [PaymentFeignService#getPaymentById] connection: keep-alive
: [PaymentFeignService#getPaymentById] content-type: application/json
: [PaymentFeignService#getPaymentById] date: Fri, 08 Oct 2021 12:15:53 GMT
: [PaymentFeignService#getPaymentById] keep-alive: timeout=60
: [PaymentFeignService#getPaymentById] transfer-encoding: chunked
: [PaymentFeignService#getPaymentById] 
: [PaymentFeignService#getPaymentById] {"code":200,"message":"查询数据成功,serverPort:8001","data":{"id":1,"serial":"test"}}
: [PaymentFeignService#getPaymentById] <--- END HTTP (91-byte body)
```



#### **==Hystrix实现服务降级熔断限流==**

Hystrix可以用于调用方和提供方，但是**Hystrix一般用于调用方**

#### 18）生产模块8001添加Hystrix

![image-20211008211817735](assets\image-20211008211817735.png)

##### jmeter压力测试

![image-20211008213531615](assets\image-20211008213531615.png)

![image-20211008213820521](assets\image-20211008213820521.png)

结论：http://localhost:8001/payment/hystrix/ok/1 访问也会变慢



#### 19）消费模块80添加Hystrix

![image-20211008215142412](assets\image-20211008215142412.png)



压力测试后，消费端进行测试 http://localhost/consumer/payment/hystrix/ok/1

要么转圈圈等待，要么消费端报超时错误

![image-20211008223338374](assets\image-20211008223338374.png)

生产者消费者添加Hystrix进行服务降级/熔断/限流

##### 生产者8001使用Hystrix进行服务降级

①主启动类添加启动注解@EnableCircuitBreaker

```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrixMain8001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8001.class,args);
    }
}
```

②service添加@HystrixCommand 进行服务降级

```java
	/**
     * paymentInfo_Timeout方法出问题了,paymentInfo_TimeoutHandler兜底处理 运行异常与超时异常都会走这个兜底方法
     * 3秒钟以内就是正常的业务逻辑，超过3秒就服务降级
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
    public String paymentInfo_Timeout(Integer id){

        //int age = 10 / 0;
        int timeout = 5000;
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程池：  "+Thread.currentThread().getName()+"   paymentInfo_Timeout,id: "+id+"  成功输出！！！！！！,耗时"+timeout+"毫秒";
    }

    /**
     * 降级（兜底）方法
     * @param id
     * @return
     */
    public String paymentInfo_TimeoutHandler(Integer id){
        return "线程池：  "+Thread.currentThread().getName()+"   系统繁忙，请稍后重试,id: "+id+"   ^_^";
    }
```

测试超时/异常情况，进入paymentInfo_TimeoutHandler降级方法，此时处理的线程池是Hystrix的线程池：

![image-20211008231757758](assets\image-20211008231757758.png)



##### 消费者80使用Hystrix进行服务降级

①修改yml

```yaml
feign:
  hystrix:
    enabled: true  #true 在feign中开启hystrix,如果处理自身的容错就开启。开启方式与生产端不一样
```

②主启动类添加启用注解@EnableHystrix

```java
@SpringBootApplication
@EnableFeignClients
@EnableHystrix
public class OrderHystrixMain80 {
    public static void main(String[] args) {
        SpringApplication.run(OrderHystrixMain80.class,args);
    }
}
```

③修改调用service方法添加注解

```java
	@GetMapping(value = "/consumer/payment/hystrix/timeout/{id}")
    @HystrixCommand(fallbackMethod = "paymentInfo_TimeoutHandler",commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value = "1500")  //1.5秒钟以内就是正常的业务逻辑
    })
    public String paymentInfo_Timeout(@PathVariable("id")  Integer id){
        //int age = 10 / 0;
        String result = paymentHystrixService.paymentInfo_Timeout(id);
        log.info("*************result："+result);
        return result;
    }
    public String paymentInfo_TimeoutHandler(@PathVariable("id")  Integer id){
        return "我是消费者80,对方支付系统繁忙请10秒后再试或者自己运行出错请检查自己   ^_^";
    }
```

测试：

消费方1.5秒超时，生产方5秒超时。异常情况。

![image-20211008234019939](assets\image-20211008234019939.png)



##### 解决代码膨胀问题：

全局降级方法设置，类上添加@DefaultProperties

```java
@RestController
@Slf4j
@DefaultProperties(defaultFallback = "payment_global_fallbackMethod") //全局兜底方法
public class OrderHystrixController {
    /**
     * 全局降级方法，配合@DefaultProperties注解使用
     * @return
     */
    public String payment_global_fallbackMethod(){
        return "global异常处理信息，请稍后再试  ^_^";
    }
}    
```

##### 解决业务混乱问题：

①新增调用接口的实现类

````java
@Component
public class PaymentFallbackService implements PaymentHystrixService{
    @Override
    public String paymentInfo_OK(Integer id) {
        return "---------PaymentFallbackService fall back paymentInfo_OK,^ ^,";
    }

    @Override
    public String paymentInfo_Timeout(Integer id) {
        return "---------PaymentFallbackService fall back paymentInfo_Timeout,^ ^,";
    }
}
````

②接口的@FeignClient注解添加 fallback属性

```java
@Component
@FeignClient(value = "HYSTRIX-PAYMENT-SERVICE",fallback = PaymentFallbackService.class)//服务降级交给 PaymentFallbackService来处理
public interface PaymentHystrixService {

    @GetMapping(value = "/payment/hystrix/ok/{id}")
    public String paymentInfo_OK(@PathVariable("id")  Integer id);

    @GetMapping(value = "/payment/hystrix/timeout/{id}")
    public String paymentInfo_Timeout(@PathVariable("id")  Integer id);
}
```

测试：停掉生产者8001模块后再次访问

![image-20211009001407808](assets\image-20211009001407808.png)



##### 生产者8001使用Hystrix进行服务熔断

```java
	/**
     * 服务熔断
     * 以下配置的意思：在20秒钟内，如果请求10次，失败了超过60%就熔断，不能使用。
     * 休眠10秒后，会将断路器置为半打开状态，尝试熔断的请求命令。如果依然失败就将断路器继续设置为打开状态，如果成功就设置为关闭状态。
     */
    @HystrixCommand(fallbackMethod = "paymentCircuitBreaker_fallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),  //是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),   //请求次数
            @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds",value = "20000"),//滚动时间
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),  //休眠时间窗
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"), //失败率达到多少后跳闸
    })
    public String paymentCircuitBreaker( Integer id){
        if (id < 0){
            throw new RuntimeException("*****id 不能负数");
        }
        String serialNumber = IdUtil.simpleUUID();

        return Thread.currentThread().getName()+"\t"+"调用成功,流水号："+serialNumber;
    }
    public String paymentCircuitBreaker_fallback( Integer id){
        return "id 不能负数，请稍候再试,(┬＿┬)/~~     id: " +id;
    }
```





#### 20）Hystrix dashboard仪表盘

9001模块监控生产模块8001

![image-20211009140710458](assets\image-20211009140710458.png)

生产模块8001添加

```java
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
public class PaymentHystrixMain8001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentHystrixMain8001.class,args);
    }
    /**
     * 此配置是为了服务器监控而配置，与服务器本身无关，springcloud升级后的坑
     * ServletRegistrationBean 因为springboot的默认路径不是"/hystrix.stream"
     * 只要在自己的项目里配置上下面的servlet就可以了
     */
    @Bean
    public ServletRegistrationBean getServlet(){
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }
}
```



#### ==Gateway网关==

#### 21）网关gateway

![image-20211009171956665](assets\image-20211009171956665.png)

从**网关实现负载均衡**，在**uri处不能把地址写死，要写微服务名称**

```yaml
spring:
  application:
    name: cloud-gateway
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true #开启从注册中心动态创建路由的功能，利用微服务名进行路由
      routes:
        - id: payment_routh #payment_route    #路由的ID，没有固定规则但要求唯一，建议配合服务名
          #uri: http://localhost:8001          #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service #匹配后提供服务的路由地址
          predicates:
            - Path=/payment/get/**         # 断言，路径相匹配的进行路由

        - id: payment_routh2 #payment_route    #路由的ID，没有固定规则但要求唯一，建议配合服务名
          #uri: http://localhost:8001          #匹配后提供服务的路由地址
          uri: lb://cloud-payment-service #匹配后提供服务的路由地址
          predicates:
            - Path=/payment/lb/**         # 断言，路径相匹配的进行路由
            #- After=2020-10-13T23:01:03.716+08:00[Asia/Shanghai]
            #- Cookie=username,qh
            #curl http://localhost:9527/payment/lb --cookie "username=qh"  curl模拟发送get请求
            #- Header=X-Request-Id, \d+  # 请求头要有X-Request-Id属性并且值为整数的正则表达式
            #curl http://localhost:9527/payment/lb -H "X-Request-Id:123"
            #- After=2020-02-21T15:51:37.485+08:00[Asia/Shanghai]
            #- Cookie=username,zzyy
            #- Header=X-Request-Id, \d+  # 请求头要有X-Request-Id属性并且值为整数的正则表达式
```



#### ==配置中心Config==

#### 22）config配置文件服务端

![image-20211009194340122](assets\image-20211009194340122.png)



#### 23）config配置文件客户端

![image-20211009204042554](assets\image-20211009204042554.png)



github修改config配置文件，3344服务端立即刷新，3355客户端不能立即读取到修改的配置文件信息。

解决办法：**客户端的动态刷新**

①添加@RefreshScope注解

```java
@RestController
@RefreshScope
public class ConfigClientController {

    /**
     * 该值为github上的配置文件的值
     */
    @Value("${config.info}")
    private String configInfo;

    @GetMapping("/configInfo")
    public String getConfigInfo()
    {
        return configInfo;
    }
}
```

②yml添加如下配置

```yaml
# 暴露监控端点，客户端动态刷新
management:
  endpoints:
    web:
      exposure:
        include: "*"
```



#### ==消息总线Bus==

#### 24）新增一个config客户端3366

![image-20211009213618007](assets\image-20211009213618007.png)

##### config配置文件服务端3344整合Bus和MQ

①引入pom文件

```xml
<!--添加消息总线RabbitMQ支持-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

②添加yml配置

```yaml
spring:
  application:
    name:  cloud-config-center #注册进Eureka服务器的微服务名
  cloud:
    config:
      server:
        git:
          uri: https://github.com/Qh92/springcloud-config.git #GitHub上面的git仓库名字
          ####搜索目录
          search-paths:
            - springcloud-config
          skip-ssl-validation: true
      ####读取分支
      label: main
  #rabbitmq相关配置
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  
#rabbitmq相关配置,暴露bus刷新配置的端点
management:
  endpoints: #暴露bus刷新配置的端点
    web:
      exposure:
        include: 'bus-refresh'  
```



##### config配置文件客户端3355添加消息总线的支持

```xml
<!--添加消息总线RabbitMQ支持-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

```yaml
spring:
  application:
    name: config-client
  cloud:
    #Config客户端配置
    config:
      label: main #分支名称
      name: config #配置文件名称
      profile: dev #读取后缀名称   上述3个综合：main分支上config-dev.yml的配置文件被读取http://config-3344.com:3344/main/config-dev.yml
      uri: http://config-3344.com:3344 #配置中心地址k

  #rabbitmq相关配置 15672是Web管理界面的端口；5672是MQ访问的端口
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

##### config配置文件客户端3366添加消息总线的支持

同上





#### ==SpringCloud Stream==

#### 25）消息提供方8801

![image-20211010132545043](assets\image-20211010132545043.png)

#### 26）消息消费方8802

![image-20211010134552296](assets\image-20211010134552296.png)

#### 27）消息消费方8803

![image-20211010152807373](assets\image-20211010152807373.png)



#### ==链路追踪Sleuth+Zipkin==

#### 28）监控生产者8001

①添加pom

```xml
<!--包含了sleuth+zipkin-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```



②修改yml

```yaml
spring:
  application:
    #应用名称
    name: cloud-payment-service
  #监控该微服务
  zipkin:
    base-url: http://localhost:9411
    sleuth:
      sampler:
      #采样率值介于 0 到 1 之间，1 则表示全部采集
      probability: 1
```



③修改controller

```java
	/**
     * 链路测试
     * @return
     */
    @GetMapping("/payment/zipkin")
    public String paymentZipkin() {
        return "hi ,i'am paymentzipkin server fall back，welcome to atguigu，O(∩_∩)O哈哈~";
    }
```



#### ==SpringCloud Alibaba==

#### 29）生产者9001、9002，消费者83

![image-20211010210306211](assets\image-20211010210306211.png)



![image-20211010210324586](assets\image-20211010210324586.png)



![image-20211010210341993](assets\image-20211010210341993.png)









## 4、热部署

见 <b>思维导图 第四章 --> 4.微服务架构编码构建 --> Rest微服务工程构建</b>  

## 5、Eureka(已停更)

![image-20211007140417671](assets\image-20211007140417671.png)





# 二、SpringCloud Alibaba