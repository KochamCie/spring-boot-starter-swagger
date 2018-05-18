package com.kochamcie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author: hama
 * @date: created in  2018/5/18
 * @description:
 */
@Slf4j
@ConditionalOnProperty(
        prefix = "swagger",
        name = {"ascii-info.enabled", "enabled"},
        havingValue = "true"
)
@AutoConfigureAfter({Swagger2DocAutoConfig.class})
@EnableWebMvc
public class Swagger2MVCAutoConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        UnderCoverProperties properties = new UnderCoverProperties();
        registry.addResourceHandler(properties.getView())
                .addResourceLocations(properties.getClasspathHtml());
    }

}