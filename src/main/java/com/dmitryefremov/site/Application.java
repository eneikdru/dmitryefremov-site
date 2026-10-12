package com.dmitryefremov.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.dmitryefremov.site",
    "com.eneik.generated.dmitryefremov.controller",
    "com.eneik.generated.dmitryefremov.service"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
