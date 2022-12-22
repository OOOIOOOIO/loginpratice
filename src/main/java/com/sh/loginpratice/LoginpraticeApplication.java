package com.sh.loginpratice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@ComponentScan(basePackages = {"com.sh.loginpratice.commonjwt"})
@ComponentScan(basePackages = {"com.sh.loginpratice.websecurityjwt"})
@SpringBootApplication
public class LoginpraticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginpraticeApplication.class, args);
    }

}
