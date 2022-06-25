package ru.duzhinsky.yandexmegamarket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YandexMegaMarketApplication implements ApplicationRunner {
    @Autowired
    private ApplicationWarmup warmupper;

    public static void main(String[] args) {
        SpringApplication.run(YandexMegaMarketApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        warmupper.warmup();
    }
}
