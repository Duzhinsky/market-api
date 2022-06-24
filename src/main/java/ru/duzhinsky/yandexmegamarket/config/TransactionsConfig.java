package ru.duzhinsky.yandexmegamarket.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Log
public class TransactionsConfig {

    @Autowired
    public LocalContainerEntityManagerFactoryBean emf;

    @Bean
    public PlatformTransactionManager transactionManager(){
        log.info(emf.toString());
        JpaTransactionManager transactionManager
                = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                emf.getObject() );
        return transactionManager;
    }
}