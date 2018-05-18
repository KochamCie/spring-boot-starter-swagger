package com.kochamcie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.configuration.Swagger2DocumentationConfiguration;

/**
 * @author: hama
 * @date: created in  2018/5/17
 * @description:
 */
@EnableSwagger2
@ConditionalOnProperty(
        prefix = "swagger",
        name = {"enabled"},
        havingValue = "true"
)
@EnableConfigurationProperties(SwaggerProperties.class)
@Import({
        Swagger2DocumentationConfiguration.class
})
@Controller
public class Swagger2AutoConfig {

    @Autowired
    SwaggerProperties properties;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(properties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * about your api information
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .termsOfServiceUrl(properties.getTermsOfServiceUrl())
                .contact(contactInfo(properties.getContact()))
                .version(properties.getVersion())
                .build();
    }


    private Contact contactInfo(SwaggerProperties.Contact contact) {
        return new Contact(contact.getName(),
                contact.getUrl(),
                contact.getEmail());

    }

}
