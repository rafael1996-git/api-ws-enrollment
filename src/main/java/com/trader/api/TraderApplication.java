package com.trader.api;

import com.trader.core.TraderCoreApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TraderApplication extends TraderCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraderApplication.class, args);
    }

}
