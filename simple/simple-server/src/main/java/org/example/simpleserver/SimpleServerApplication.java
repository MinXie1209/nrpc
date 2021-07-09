package org.example.simpleserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class SimpleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleServerApplication.class, args);
    }

}
