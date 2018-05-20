package com.kochamcie;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import io.swagger.models.Swagger;
import lombok.extern.slf4j.Slf4j;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;
import springfox.documentation.service.Documentation;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import javax.annotation.PostConstruct;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.asciidoctor.Asciidoctor.Factory.create;

/**
 * doc file auto config
 *
 * @author : hama
 * @since : created in  2018/5/17
 */
@Slf4j
@ConditionalOnProperty(
        prefix = "swagger",
        name = {"ascii-info.enabled", "enabled"},
        havingValue = "true"
)

@EnableConfigurationProperties(SwaggerProperties.class)
@AutoConfigureAfter({Swagger2AutoConfig.class,
        ServiceModelToSwagger2Mapper.class,
        DocumentationCache.class,
        ApplicationReadyEvent.class})
@Controller
public class Swagger2DocAutoConfig {

    @Autowired
    SwaggerProperties properties;

    @Autowired(required = false)
    private ServiceModelToSwagger2Mapper mapper;

    @Autowired(required = false)
    private DocumentationCache documentationCache;

    private UnderCoverProperties underCover;

    private boolean pathInsurance = false;

    @PostConstruct
    public void init() {

        if (!properties.getAsciiInfo().isEnabled()) {
            return;
        }
        URL classpath = ClassUtils.getDefaultClassLoader().getResource("");
        underCover = new UnderCoverProperties();
        log.info("{}", underCover.toString());
        String path = classpath.getPath() + underCover.getStaticLocation();
        pathInsurance = pathInsurance(path);
        log.info("Swagger2Doc.init: {}", pathInsurance);
    }

    /**
     * check given path exist
     *
     * @param path path
     * @return check given path exist
     */
    public boolean pathInsurance(String path) {
        log.info("pathInsurance:{}", path);
        File file = new File(path);
        log.info("{}", file.exists());
        if (!file.exists()) {
            log.info("file.exists():{}", file.exists());
            return file.mkdirs();
        }
        return true;
    }


    @Bean
    public InitSwaggerDocRunner initSwaggerDocRunner() {
        return new InitSwaggerDocRunner();
    }

    public class InitSwaggerDocRunner implements ApplicationContextAware, ApplicationRunner, Ordered {

        private ApplicationContext applicationContext;

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }


        @Override
        public int getOrder() {
            return 2;
        }

        @Override
        public void run(ApplicationArguments args) throws Exception {
            log.info("InitSwaggerDocRunner runner");

            if (!pathInsurance) {
                log.info("pathInsurance is false!!!");
                return;
            }

            if (!desire()) {
                log.info("init skipped");
                return;
            }
            Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                    .withPathsGroupedBy(GroupBy.TAGS)
                    .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                    .build();

            SwaggerProperties.AsciiInfo info = properties.getAsciiInfo();
            if (null == info) {
                log.info("ascii info skipped");
                return;
            }
            String groupName = (String) Optional.fromNullable(null).or("default");
            Documentation documentation = documentationCache.documentationByGroup(groupName);
            Swagger swagger = mapper.mapDocumentation(documentation);

            //
            if (Strings.isNullOrEmpty(swagger.getHost())) {
                if (null == applicationContext) {
                    System.out.println("not ready too");
                } else {
                    swagger.setHost("localhost:" + getPort(applicationContext));
                    ServletContext context = ((AnnotationConfigEmbeddedWebApplicationContext) applicationContext).getServletContext();
                    swagger.setBasePath(context.getContextPath());
                }

            }

            File file = ResourceUtils.getFile(underCover.getClasspathHtml());

            Swagger2MarkupConverter.from(swagger)
                    .withConfig(config)
                    .build()
                    .toFile(Paths.get(file.getPath(), underCover.getDocName()));

            Options options = new Options();
            options.setBackend(underCover.getBackend());
            Map<String, Object> map = new HashMap<>();
            map.put("toc", properties.getAsciiInfo().getToc());
            options.setAttributes(map);
            Asciidoctor asciidoctor = create();
            asciidoctor.convertFile(
                    ResourceUtils.getFile(underCover.getClasspathAdoc()),
                    options);
            log.info("done!");
        }

        private boolean desire() {
            File file;
            boolean initEveryTime = properties.getAsciiInfo().isInitEveryTime();
            try {
                file = ResourceUtils.getFile(underCover.getClasspathAdoc());
            } catch (FileNotFoundException e) {
                log.debug("FileNotFound  need to init, ignore initEveryTime[{}]", initEveryTime);
                return true;
            }

            if (file.exists() && !initEveryTime) {
                log.debug("file.exists[{}],initEveryTime[{}]", file.exists(), initEveryTime);
                return false;
            }
            log.debug("file.exists[{}],initEveryTime[{}]", file.exists(), initEveryTime);
            return true;
        }


        String getPort(ApplicationContext applicationContext) {
            String port = getOuterPort();
            if (UnderCoverProperties.UNKNOWN_PORT.equalsIgnoreCase(port)) {
                int temp = getEmbeddedPort(applicationContext);
                return 0 == temp ? null : String.valueOf(temp);
            }
            return port;
        }

        int getEmbeddedPort(ApplicationContext applicationContext) {
            EmbeddedServletContainer container = ((AnnotationConfigEmbeddedWebApplicationContext) applicationContext).getEmbeddedServletContainer();
            return container.getPort();
        }

        String getOuterPort() {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            Set<ObjectName> objectNames;
            try {
                objectNames = mbs.queryNames(new ObjectName("*:type=Connector,*"),
                        Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
            } catch (MalformedObjectNameException e) {
                e.printStackTrace();
                return UnderCoverProperties.UNKNOWN_PORT;
            }
            for (Iterator<ObjectName> i = objectNames.iterator(); i.hasNext(); ) {
                ObjectName obj = i.next();
                String port = obj.getKeyProperty(UnderCoverProperties.KEY_PORT);
                return port;
            }
            return UnderCoverProperties.UNKNOWN_PORT;
        }

    }

}
