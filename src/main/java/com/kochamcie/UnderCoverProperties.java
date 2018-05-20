package com.kochamcie;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * reserved properties
 *
 * @author : hama
 * @since : created in  2018/5/17
 */
@Data
public class UnderCoverProperties {

    public static final String KEY_PORT = "port";

    public static final String UNKNOWN_PORT = "-1";

    /**
     * 生成资源存放
     */
    private String staticLocation = "static/swagger/";


    private String docName = "all";


    /**
     * classpath:static/swagger/all.adoc
     * adoc所在路径
     */
    private String classpathAdoc = "";


    private String doc = "all.adoc";

    /**
     * classpath:static/swagger/
     * view所在路径
     */
    private String classpathHtml = "";

    /**
     * todoc返回的视图
     */
    private String view = "all.html";

    private String backend = "html";

    private UnderCoverProperties() {
    }

    private static UnderCoverProperties coverProperties = null;

    public static UnderCoverProperties getInstance() {

        if (null == coverProperties) {
            coverProperties = new UnderCoverProperties();
        }
        return coverProperties;
    }


    public static String safePath(String staticLocation) {

        File path ;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());

            if (!path.exists()) path = new File("");

            File upload = new File(path.getAbsolutePath(), staticLocation);
            if (!upload.exists()) upload.mkdirs();
            return upload.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
