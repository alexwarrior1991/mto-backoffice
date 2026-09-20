package com.alejandro.mtobackoffice;

import org.springframework.boot.SpringApplication;

public class TestMtoBackofficeApplication {

    public static void main(String[] args) {
        SpringApplication.from(MtoBackofficeApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
