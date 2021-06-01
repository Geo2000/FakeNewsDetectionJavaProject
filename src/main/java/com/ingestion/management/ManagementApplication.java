package com.ingestion.management;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class ManagementApplication implements CommandLineRunner {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
