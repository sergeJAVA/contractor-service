package com.example.contractor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ContractorServiceApplication {

public static void main(String[] args) {

SpringApplication.run(ContractorServiceApplication.class, args);

}

}
