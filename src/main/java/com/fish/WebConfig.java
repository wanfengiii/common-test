package com.fish;

import com.common.json.PageMixin;
import com.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.inesat.trace.view.document.doc.excel.ExcelDocFactory;
import com.inesat.trace.view.document.doc.excel.ExcelView;
import com.inesat.trace.view.document.doc.pdf.PdfDocFactory;
import com.inesat.trace.view.document.doc.pdf.PdfView;
import com.inesat.trace.view.document.doc.style.StyleConfig;
import com.inesat.trace.view.document.doc.style.StyleConfigFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.common.util.DateUtils.*;

/**
 * DO NOT EnableWebMvc, coz the static content within the war will not be
 * accessible
 * 
 * spring boot doc: As in normal MVC usage, any WebMvcConfigurer beans that you
 * provide can also contribute converters by overriding the
 * configureMessageConverters method. However, unlike with normal MVC, you can
 * supply only additional converters that you need (because Spring Boot uses the
 * same mechanism to contribute its defaults). Finally, if you opt out of the
 * Spring Boot default MVC configuration by providing your own @EnableWebMvc
 * configuration, you can take control completely and do everything manually by
 * using getMessageConverters from WebMvcConfigurationSupport.
 * 
 * https://stackoverflow.com/questions/24661289/spring-boot-not-serving-static-content/26088252
 * 
 * @EnableWebMvc on your class will disable
 *               org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration
 * 
 */
// @EnableWebMvc
@Configuration
@EnableSpringDataWebSupport
@Log4j2
public class WebConfig implements WebMvcConfigurer {

    @Value("${swagger.basic.enable}")
    private boolean enabled;

    private static final long DEFAULT_MAX_UPLOAD_SIZE = 5 * 1024 * 1024;    // 5M

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar tf = new DateTimeFormatterRegistrar();
        tf.setDateTimeFormatter(DATETIME_FORMATTER);
        tf.setDateFormatter(DATE_FORMATTER);
        tf.setTimeFormatter(TIME_FORMATTER);
        tf.registerFormatters(registry);

        registry.addConverterFactory(getEnumConverterFactory());
    }

    private ConverterFactory<String, Enum> getEnumConverterFactory() {
        return new ConverterFactory<String, Enum>() {

            @Override
            public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
                return s -> (T) Enum.valueOf(targetType, s.toUpperCase());
            }
        };
    }

    /**
     * spring boot doc:
     * 
     * 9.4.3. Customize the Jackson ObjectMapper If you provide any @Beans of type
     * MappingJackson2HttpMessageConverter, they replace the default value in the
     * MVC configuration.
     */
    @Bean
    public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
        ObjectMapper om = Jackson2ObjectMapperBuilder
            .json()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .mixIn(PageImpl.class, PageMixin.class) // customize json serialization for PageImpl.class
            .modules(new JavaTimeModule())
            // set default serialization/deserialization format for LocalDate and LocalDateTime
            .serializers(new LocalDateSerializer(DateUtils.DATE_FORMATTER))
            .deserializers(new LocalDateDeserializer(DateUtils.DATE_FORMATTER))
            .serializers(new LocalDateTimeSerializer(DateUtils.DATETIME_FORMATTER))
            .deserializers(new LocalDateTimeDeserializer(DateUtils.DATETIME_FORMATTER))
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
        return new MappingJackson2HttpMessageConverter(om);
    }

    /**
     * for file upload
     */
    @Bean
    public CommonsMultipartResolver commonsMultipartResolver(
            @Value("${filestorage.upload.maxUploadSize:" + DEFAULT_MAX_UPLOAD_SIZE + "}") long bytes
        ) {

        CommonsMultipartResolver commonsMultipartResolver =  new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("UTF-8");
        commonsMultipartResolver.setMaxUploadSize(bytes);
        return commonsMultipartResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String root = new ApplicationHome().getDir().getAbsolutePath();
        root = FilenameUtils.normalize(root, true);
        if (!root.endsWith("/")) {
            root = root + "/";
        }
        addFolder(registry, "web", root);
        addFolder(registry, "app", root);

        if(enabled) {
            registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }

    private void addFolder(ResourceHandlerRegistry registry, String folder, String root) {
        String pattern = "/" + folder + "/**";
        String path = "file:" + root + folder + "/";
        registry.addResourceHandler(pattern).addResourceLocations(path);
        log.info("adding resource {} to pattern {}", path, pattern);
    }

    @Bean
    public RestTemplate restTemplate(@Value("${rest.http.timeout:60000}") int timeout) {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        clientHttpRequestFactory.setReadTimeout(timeout);
        return new RestTemplate(clientHttpRequestFactory);
    }

    @Bean
    @ConfigurationProperties(prefix = "doc.style")
    public StyleConfig styleConfig() {
        return new StyleConfigFactory().getDefaultStyleConfig();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        StyleConfig sc = styleConfig();
        log.debug("StyleConfig: {}", sc);
        ExcelDocFactory edf = new ExcelDocFactory(sc);
        PdfDocFactory pdf = new PdfDocFactory(sc);
        registry.enableContentNegotiation(new ExcelView(edf), new PdfView(pdf));
        registry.jsp();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorParameter(true);
        configurer.mediaType("xls", MediaType.parseMediaType("application/vnd.ms-excel"));
        configurer.mediaType("pdf", MediaType.APPLICATION_PDF);
    }

}