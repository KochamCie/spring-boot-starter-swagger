package com.kochamcie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * config all.html reachable
 *
 * @author : hama
 * @since : created in  2018/5/18
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
        UnderCoverProperties properties = UnderCoverProperties.getInstance();
        registry.addResourceHandler(properties.getView())
                .addResourceLocations(properties.getClasspathHtml());
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("asciidoctor.css")
                .addResourceLocations("classpath:/META-INF/resources/");
        log.info("UnderCoverProperties.getInstance().getClasspathHtml() is :{}", UnderCoverProperties.safePath(properties.getStaticLocation()));
        String path = UnderCoverProperties.safePath(properties.getStaticLocation());
        log.info("path is :{}", path);
        if (!path.contains("classes")) {
            log.info("path.contains(\"classes\")", path.contains("classes"));
            registry.addResourceHandler("all.html")
                    .addResourceLocations(path);
        } else {
            log.info("use default all.html static path");
        }
    }

}
