package com.kochamcie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

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
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("asciidoctor.css")
                .addResourceLocations("classpath:/META-INF/resources/");
        log.info("UnderCoverProperties.getInstance().getClasspathHtml() is :{}", UnderCoverProperties.safePath(properties.getStaticLocation()));
        String path = UnderCoverProperties.safePath(properties.getStaticLocation());
        log.info("path is :{}", path);
        if (!path.contains("classes")) {
            log.info("path.contains(\"classes\")", path.contains("classes"));
            registry.addResourceHandler(properties.getView())
                    .addResourceLocations(path);
        } else {
            log.info("use default all.html static path classpath:static/swagger/");
            registry.addResourceHandler(properties.getView())
                    .addResourceLocations("classpath:static/swagger/");
        }
    }

}
