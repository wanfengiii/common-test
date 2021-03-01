package com.common.file;

import com.common.exceptions.FileException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface FileService {

    String CONTROLLER_PATH = "/v1/files";

    String CONTENT_PATH = "/content";

    String DOWNLOAD_PATH = "/download";

    String guessContentType(String pathname);

    String write(String pathname, InputStream is, String contentType) throws FileException;

    default String write(String pathname, InputStream is) throws FileException {
        return write(pathname, is, null);
    }

    void deleteFile(String pathname) throws FileException;

    void deletePath(String pathname) throws FileException;

    boolean exists(String pathname) throws FileException;

    /**
     * upload multiple files within one request
     * @return key, name passed from the client; value, pathname
     */
    Map<String, String> uploads(MultipartHttpServletRequest request, String folder, boolean overwrite);

    default Map<String, String> uploads(MultipartHttpServletRequest request, String folder) {
        return uploads(request, folder, false);
    }

    /**
     * upload one file within the request
     * return the first file pathname if multiple files found
     */
    String upload(MultipartHttpServletRequest request, String folder, boolean overwrite);

    default String upload(MultipartHttpServletRequest request, String folder) {
        return upload(request, folder, false);
    }

    InputStream get(String pathname) throws FileException;

    List<String> getUrl(List<String> pathnames, boolean download);

    default List<String> getUrl(List<String> pathnames) {
        return getUrl(pathnames, false);
    }

    default String getUrl(String pathname) {
        List<String> urls = getUrl(Collections.singletonList(pathname), false);
        return (CollectionUtils.isEmpty(urls) ? null : urls.get(0));
    }

}