package com.twowheeler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TwoWheelerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwoWheelerApplication.class, args);
        System.out.println("=================================================");
        System.out.println("Two-Wheeler Service Management System Started!");
        System.out.println("Backend & Frontend available at: http://localhost:8080");
        System.out.println("=================================================");
    }
}
