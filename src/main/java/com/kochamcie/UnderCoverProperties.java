package com.kochamcie;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * reserved properties
 *
 * @author : hama
 * @since : created in  2018/5/17
 */
@Data
@NoArgsConstructor
public class UnderCoverProperties {

    public static final String KEY_PORT = "port";

    public static final String UNKNOWN_PORT = "-1";

    /**
     * 生成资源存放
     */
    private String staticLocation = "static/swagger/";


    private String docName = "all";


    /**
     * adoc所在路径
     */
    private String classpathAdoc = "classpath:static/swagger/all.adoc";


    private String doc = "all.adoc";

    /**
     * view所在路径
     */
    private String classpathHtml = "classpath:static/swagger/";

    /**
     * todoc返回的视图
     */
    private String view = "all.html";

    private String backend = "html";


}
