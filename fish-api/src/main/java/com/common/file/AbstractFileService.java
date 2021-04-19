package com.common.file;

import com.common.exceptions.FileException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.*;

@Log4j2
public abstract class AbstractFileService implements FileService, InitializingBean {

    private String UNIX_SEPARATOR = "/";

    private Map<String, String> contentTypeMap;

    private static final String XLS = "application/vnd.ms-excel";

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    protected String domain = "";

    protected Long expires = 60000L;

    protected String secretKey = "";

    protected ObjectMapper objectMapper;

    public void setDomain(String domain) {
        this.domain = domain;
    }
    public void setExpires(Long expires) {
        this.expires = expires;
    }
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setObjectMapper(ObjectMapper om) {
        this.objectMapper = om;
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        initMappings();
    }

    private void initMappings() {
        Map<String, String> contentTypeMap = new HashMap<>();
        contentTypeMap.put("pdf", APPLICATION_PDF_VALUE);
        contentTypeMap.put("gif", IMAGE_GIF_VALUE);
        contentTypeMap.put("jpeg", IMAGE_JPEG_VALUE);
        contentTypeMap.put("png", IMAGE_PNG_VALUE);
        contentTypeMap.put("txt", TEXT_PLAIN_VALUE);
        contentTypeMap.put("xml", APPLICATION_XML_VALUE);
        contentTypeMap.put("html", TEXT_HTML_VALUE);
        contentTypeMap.put("json", APPLICATION_JSON_VALUE);
        contentTypeMap.put("xls", XLS);
        contentTypeMap.put("xlsx", XLSX);
        contentTypeMap.put("jpg", IMAGE_JPEG_VALUE);
        this.contentTypeMap = ImmutableMap.copyOf(contentTypeMap);
    }

    @Override
    public String guessContentType(String pathname) {
        String ext = FilenameUtils.getExtension(pathname);
        if (StringUtils.isBlank(ext)) {
            return APPLICATION_OCTET_STREAM_VALUE;
        }
        return contentTypeMap.get(ext.toLowerCase());
    }

    public Map<String, String> uploads(MultipartHttpServletRequest request, String folder, boolean overwrite) {
        Map<String, String> result = new HashMap<>();
        for (Iterator<String> it = request.getFileNames(); it.hasNext();) {
            String name = it.next();
            MultipartFile file = request.getFile(name);
            String pathname = buildPathname(folder, file.getOriginalFilename());
            pathname = upload(pathname, file, overwrite);
            result.put(name, pathname);
        }
        return result;
    }

    private String buildPathname(String folder, String filename) {
        folder = FilenameUtils.separatorsToUnix(folder);
        folder = StringUtils.removeStart(folder, UNIX_SEPARATOR);    // minIO do not accept starting '/'
        filename = FilenameUtils.separatorsToUnix(filename);
        return FilenameUtils.normalize(folder + UNIX_SEPARATOR + filename, true);
    }

    private String upload(String pathname, MultipartFile file, boolean overwrite) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (!overwrite) {
            // concurrency is not taken into consideration
            pathname = suggestName(pathname);
        }

        doUpload(pathname, file);

        return pathname;
    }

    private void doUpload(String pathname, MultipartFile file) {
        try (InputStream in = file.getInputStream();) {
            write(pathname, in);
        } catch (IOException e) {
            throw new FileException("failed to upload file " + pathname, e);
        }
    }

    /**
     * return the input pathname if the input pathname not exists,
     * return a new suggest name if the input pathname exists.
     */
    protected abstract String suggestName(String pathname);

    @Override
    public String upload(MultipartHttpServletRequest request, String folder, boolean overwrite) {
        Map<String, String> result = uploads(request, folder, overwrite);
        if (CollectionUtils.isEmpty(result)) {
            log.warn("no file uploaded to {}", folder);
            return null;
        }
        if (result.size() > 1) {
            log.warn("{} files uploaded, should use uploads instead of upload", result.size());
        }
        return result.values().iterator().next();
    }

    @Override
    public String upload(String pathname, InputStream is, boolean overwrite) {
        if (Objects.isNull(is)) {
            return null;
        }

        if (!overwrite) {
            // concurrency is not taken into consideration
            pathname = suggestName(pathname);
        }

        return write(pathname,is);
    }

    @Override
    public List<String> getUrl(List<String> pathnames, boolean download) {
        return pathnames.stream()
                .map(p -> getUrl(p, download))
                .collect(Collectors.toList());
    }

    protected String getUrl(String pathname, boolean download) {
        String s = (download ? DOWNLOAD_PATH : CONTENT_PATH);
        return (domain + CONTROLLER_PATH + s + "/" + pathname);
    }

}