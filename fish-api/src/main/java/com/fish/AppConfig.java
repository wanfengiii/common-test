package com.fish;

import com.common.ModelMapperFactory;
import com.common.file.FileService;
import com.common.file.LocalFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableCaching
@Log4j2
public class AppConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        return ModelMapperFactory.getObject();
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder){
        return builder.build();
    }

    @ConditionalOnMissingBean
    @Bean
    public FileService fileService(
            @Value("${filestorage.local.path:}") String path,
            @Value("${filestorage.domain:}") String domain,
            @Value("${filestorage.local.expires:86400}") Long expires,
            @Value("${filestorage.local.secretKey:tracekey}") String secretKey,
            ObjectMapper om)
            throws Exception {

        if (StringUtils.isBlank(path)) {
            path = System.getProperty("java.io.tmpdir");
        }
        path = StringUtils.removeEnd(path,"/");
        LocalFileService fs = new LocalFileService(path);
        fs.setDomain(domain);
        fs.setExpires(expires);
        fs.setSecretKey(secretKey);
        fs.setObjectMapper(om);
        log.debug("LocalFileService initialized");
        return fs;
    }
}
