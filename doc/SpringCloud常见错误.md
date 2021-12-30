# SpringCloud常见错误





### 1、java.net.UnknownHostException: CLOUD-PAYMENT-SERVICE

```java
org.springframework.web.util.NestedServletException: Request processing failed; nested exception is org.springframework.web.client.ResourceAccessException: I/O error on POST request for "http://CLOUD-PAYMENT-SERVICE/payment/create": CLOUD-PAYMENT-SERVICE; nested exception is java.net.UnknownHostException: CLOUD-PAYMENT-SERVICE
	
Caused by: org.springframework.web.client.ResourceAccessException: I/O error on POST request for "http://CLOUD-PAYMENT-SERVICE/payment/create": CLOUD-PAYMENT-SERVICE; nested exception is java.net.UnknownHostException: CLOUD-PAYMENT-SERVICE
	

2021-12-30 20:52:29.064 ERROR [cloud-order-service,,,] 22004 --- [p-nio-80-exec-4] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is org.springframework.web.client.ResourceAccessException: I/O error on POST request for "http://CLOUD-PAYMENT-SERVICE/payment/create": CLOUD-PAYMENT-SERVICE; nested exception is java.net.UnknownHostException: CLOUD-PAYMENT-SERVICE] with root cause

java.net.UnknownHostException: CLOUD-PAYMENT-SERVICE
	
```



#### 原因

1. 问题原因，由于你自己的微服务提供者设置了集群 你通过访问注册中心的注册名  如： CLOUD-XXXXX-SERVICE
  注册中心并不知道你要访问你集群下面的哪一个服务，所以报错、

2. 问题报错截取页面：

  ```java
  java.net.UnknownHostException
  java.net.UnknownHostException: CLOUD-PAYMENT-SERVICE
  at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:184) ~[na:1.8.0_251]
  at java.net.PlainSocketImpl.connect(PlainSocketImpl.java:172) ~[na:1.8.0_251]
  at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392) ~[na:1.8.0_251]
  at java.net.Socket.connect(Socket.java:606) ~[na:1.8.0_251]
  at java.net.Socket.connect(Socket.java:555) ~[na:1.8.0_251]
  at sun.net.NetworkClient.doConnect(NetworkClient.java:180) ~[na:1.8.0_251]
  at sun.net.www.http.HttpClient.openServer(HttpClient.java:463) ~[na:1.8.0_251]
  at sun.net.www.http.HttpClient.openServer(HttpClient.java:558) ~[na:1.8.0_251]
  at sun.net.www.http.HttpClient.<init>(HttpClient.java:242) ~[na:1.8.0_251]
  at sun.net.www.http.HttpClient.New(HttpClient.java:339) ~[na:1.8.0_251]
  at sun.net.www.http.HttpClient.New(HttpClient.java:357) ~[na:1.8.0_251]
  at 
  ```

  

3. 问题解决方式 负载均衡 ：我们通过消费者 使用RestTemplate 去调用服务提供者 我们需要开启负载均衡。

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

  