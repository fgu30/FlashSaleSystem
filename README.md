# 双11互联网电商秒杀解决方案

## 高并发秒杀系统核心技术

- 分布式系统方案

从单机到集群，易于横向扩展只需增加服务器即可应对更大的流量和并发



- 系统极致优化

浏览器缓存/Nginx缓存/页面缓存/对象缓存/RabbitMQ队列异步下单，减少网络流量，减轻数据库压力，全面提升系统并发处理能力



- 深入微服务技能

SpringBoot/RabbitMQ/Redis/MySQL，基于时下最火热的Java微服务框架



- 安全策略

图形险证码、限流防刷、接口地址隐藏，多种安全机制拒绝机器人刷票党



## 分布式会话

企业中用户登陆的防护手段以及集群部署下Session不一致的问题解决.

- 用户登录

  2次MD5加密

- 共享Session

  SpringSession实现分布式Session

  Redis存储用户信息

  

  **解决方案：**
  ***Session复制***
  优点
  	无需修改代码，只需要修改Tomcat配置
  缺点
  	Session同步传输占用内网带宽
  	多台Tomcat同步性能指数级下降
  	sesion占用内存，无法有效水平扩展I

  

  ***前端存储***
  优点
  	不占用服务端内存
  缺点
  	存在安全风险
  	数据大小受cookic限制
  	占用外网带宽

  

  ***Session粘滞***
  优点
  	无需修改代码
  	服务端可以水平扩展
  缺点
  	增加新机器，会重新Hash，导致里新登录
  	应用重启，需要重新登录
  	后端集中存储

  

  ***后端集中存储***
  优点
  	安全
  	容易水平扩展

  缺点
  	增加复杂度
  	需要修改代码

  

---

## 功能开发

常规秒杀功能实现，编码的规范以及编码的优化，让代码更优雅。

- 商品列表
- 商品详情
- 秒杀
- 订单详情



---

## 系统压测

本章节主要介绍了企业中最常用的压力测试工具以及一些常用的测试手段，通过这些手段可以快速了解接口能够承受的极限在哪里。

- JMeter

  单一用户测试

- 自定义变量

  批量生成用户，模拟多用户高并发访问



---

## 页面优化

本章节主要对秒杀进行了一些页面优化，通过缓存极大的加快页面访问速度。同时最主要解决了秒杀中库存超卖的致命缺陷。

- 缓存

  浏览器缓存、Nginx缓存、页面缓存、对象缓存

- 静态化分离

  ajax异步加载页面数据

- 库存超卖

  更新时加库存大于0的条件



---

## 接口优化

作了一些接口优化，包括使用Redis预防库存超卖，使用RabbitMQ进行削峰降低数据库的压力。这些手段一般都是秒杀必备的经典手段。

- RabbitMQ消息队列

  RabbitMQ队列异步下单

- 接口优化

  redis预减库存、内存标记减少对redis访问

  



---

## 安全优化

通过一些安全优化去预防一些恶意脚本或者黄牛抢到过多商品造成的损失。除了秒杀之外一些常用接口也是可以使用这些安全优化的。

- 隐藏接口地址

  将随机数作为路径参数存入redis，访问时取出路径参数，对比页面传入的路径。

- 图形验证码

  随机生成数字公式验证码图片，将结果存入redis。提交验收码时，取出结果对比输入的验证码。

- 接口限流

  - 将用户的访问次数存入redis，并设置生效时间，访问时取出判断是否达到访问限制次数。

  - 自定义限流注解，重写HandlerInterceptorAdapter.preHandle()方法，将用户信息存入ThreadLocal，拦截方法获取限流注解，将用户的访问次数存入redis，并设置生效时间，访问时取出判断是否达到访问限制次数。

  

  