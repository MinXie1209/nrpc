## nRPC简介

nRPC是一款使用Java语言开发的RPC框架。 基本功能：

- 服务发现
- 负载均衡
- 异步调用
- ...

## 依赖

- JDK 1.8+

## 快速开始

### SpringBoot

nRPC封装SpringBoot Starter，方便使用SpringBoot快速集成。

#### API

api是需要提供的服务接口，可以是Java接口或类。

1. 定义服务端客户端公共api

```java
package org.example.nrpc.simple.api;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcService;
import org.example.nrpc.common.listener.RpcCompletableFuture;

import java.util.concurrent.Future;

@NRpcService
public interface OrderService {
    void testVoid(String orderNo);

    RpcCompletableFuture<Integer> testReturn(String orderNo);
}
```

> 通过@NRpcService声明接口<br/>
> 框架使用异步调用方式，方法返回值可使用RpcCompletableFuture<T>，RpcCompletableFuture实现了java.util.Future<T>，
> 可调用Future.get()获取返回值。

#### 服务端

1. 引入依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.example</groupId>
        <artifactId>n-rpc-spring-boot-starter</artifactId>
        <version>Latest Version</version>
    </dependency>
</dependencies>
```

> 默认使用Nacos作为服务注册发现组件，可通过exclusions去掉Nacos依赖，替换为Zookeeper。<br/>
> 框架提供Nacos|Zookeeper实现的服务注册发现组件，也可自定义实现。

```xml

<dependencies>
    <dependency>
        <groupId>org.example</groupId>
        <artifactId>n-rpc-spring-boot-starter</artifactId>
        <version>Latest Version</version>
        <exclusions>
            <exclusion>
                <groupId>org.example</groupId>
                <artifactId>n-rpc-register-nacos</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <groupId>org.example</groupId>
        <artifactId>n-rpc-register-zookeeper</artifactId>
        <version>Latest Version</version>
    </dependency>
</dependencies>
```

2. 编写yml配置

```yaml
nrpc:
  register:
    connectString: 127.0.0.1:8848
  server:
    address: 127.0.0.1
    port: 9000
```

> nrpc.register.connectString是服务注册发现地址，这里使用的是Nacos的地址。<br/>
> nrpc.server是服务暴露端口地址信息。

3. 实现接口并声明实现

```java
package org.example.simpleserver.impl;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcServiceImpl;
import org.example.nrpc.common.listener.RpcCompletableFuture;
import org.example.nrpc.simple.api.OrderService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@NRpcServiceImpl(OrderService.class)
public class OrderServiceImpl implements OrderService {
    @Override
    public void testVoid(String orderNo) {
        //业务...
    }

    @Override
    public RpcCompletableFuture<Integer> testReturn(String orderNo) {
        return RpcCompletableFuture.completedFuture((int) (Math.random() * 10));
    }
}
```

> 使用注解@NRpcServiceImpl()声明接口实现，传入参数为接口类。<br/>
> 使用RpcCompletableFuture.completedFuture()返回值。

#### 客户端

1. 引入依赖

> 同服务端。

2. 编写yml配置

```yaml
nrpc:
  register:
    connectString: 127.0.0.1:8848
  client:
    enabled: true
```

> nrpc.register.connectString是服务注册发现地址，这里使用的是Nacos的地址。<br/>
> nrpc.client.enabled开启客户端功能，默认关闭。

3. 扫描定义的接口

```java

@NRpcServiceScan({"org.example.nrpc.simple.api"})
public class NRpcServiceConf {
}
```

> 使用@NRpcServiceScan() 扫描定义的接口包，入参是包名数组，可以扫描多个包。

4.调用RPC

```java

@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {
    @GetMapping("/test/{id}")
    public void test(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        BeanFactory.getBean(OrderService.class).testB(id).addListener((future) -> {
            if (future.isDone()) {
                try {
                    log.debug("操作完成：{}", future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                log.debug("操作失败");
            }
        });
    }
}
```

> 使用BeanFactory.getBean(Class)获取接口代理类,<br/>
> 可调用RpcCompletableFuture.addListener(RpcListener listener)添加监听，RPC返回会触发监听方法operationComplete。<br/>
> 也可使用RpcCompletableFuture.get()阻塞等待RPC返回。

### Java

