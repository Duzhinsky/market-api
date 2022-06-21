package ru.duzhinsky.yandexmegamarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class YandexMegaMarketApplication {

    @RequestMapping("/")
    public String hello() {
        return "Hello world!";
    }

    public static void main(String[] args) {
        SpringApplication.run(YandexMegaMarketApplication.class, args);
    }

}
