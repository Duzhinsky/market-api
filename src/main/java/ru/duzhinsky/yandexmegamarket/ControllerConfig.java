package ru.duzhinsky.yandexmegamarket;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.WebRequest;

@Configuration
public class ControllerConfig {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String ,Object> getErrorAttributes(
                    WebRequest webRequest
                    , ErrorAttributeOptions options
            ) {
                Map<String ,Object> errorAttributes = new LinkedHashMap<>();
                errorAttributes.put( "code", 400);
                errorAttributes.put( "message" , "Validation Failed");
                return errorAttributes;
            }
        };
    }
}