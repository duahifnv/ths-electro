package org.envelope.imageservice.config;

import org.envelope.imageservice.service.ImageService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ServiceFactory {
    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public ImageService createService(String name) {
        return new ImageService(name);
    }
}
