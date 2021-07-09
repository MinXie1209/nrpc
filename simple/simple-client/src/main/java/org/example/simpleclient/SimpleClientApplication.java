package org.example.simpleclient;

import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcServiceScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@SpringBootApplication
public class SimpleClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleClientApplication.class, args);
    }

}
