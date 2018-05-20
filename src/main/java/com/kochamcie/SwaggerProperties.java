package com.kochamcie;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * common properties of swagger and asciiDoc
 *
 * @author : hama
 * @since : created in  2018/5/17
 */
@Data
@ConfigurationProperties("swagger")
public class SwaggerProperties {


    /**
     * swagger switch, default false
     */
    private boolean enabled = false;


    /**
     * api doc title
     **/
    private String title = "";

    /**
     * api doc description
     */
    private String description = "";

    /**
     * terms of service
     */
    private String termsOfServiceUrl = "";


    /**
     * contact
     */
    private Contact contact = new Contact();

    /**
     * api doc version
     */
    private String version = "";

    /**
     * your api package which swagger scans
     */
    private String basePackage = "";

    /**
     * asciiInfo for your api.html
     */
    private AsciiInfo asciiInfo = new AsciiInfo();


    @Data
    @NoArgsConstructor
    public static class Contact {

        /**
         * contact name
         **/
        private String name = "";
        /**
         * contact url
         **/
        private String url = "";
        /**
         * contact email
         **/
        private String email = "";

    }

    @Data
    @NoArgsConstructor
    public static class AsciiInfo {

        /**
         * switch of asciiDoc
         */
        private boolean enabled = false;

        /**
         * toc position
         */
        private String toc = "left"; //=left

        /**
         * the ascii file init every start up
         */
        private boolean initEveryTime = false;

    }


}


