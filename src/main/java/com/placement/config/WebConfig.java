package com.placement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Resume files
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // Profile photos
        registry.addResourceHandler("/photos/**")
                .addResourceLocations("file:uploads/photos/");
    }
}