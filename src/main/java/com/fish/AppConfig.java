package com.fish;

import com.common.ModelMapperFactory;
import com.common.file.FileService;
import com.common.file.LocalFileService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@Log4j2
public class AppConfig {
    
    @Bean
    public ModelMapper modelMapper() {
        return ModelMapperFactory.getObject();
    }

    @ConditionalOnMissingBean
    @Bean
    public FileService fileService(
        @Value("${filestorage.local.path:}") String path,
        @Value("${filestorage.domain:}") String domain) 
        throws Exception {

        if (StringUtils.isBlank(path)) {
            path = System.getProperty("java.io.tmpdir");
        }

        LocalFileService fs = new LocalFileService(path);
        fs.setDomain(domain);
        log.debug("LocalFileService initialized");
        return fs;
    }
}
