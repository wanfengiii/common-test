package com.common.file;

import com.common.exceptions.FileException;
import com.common.util.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface FileService {

    String CONTROLLER_PATH = "/v1/files";

    String CONTENT_PATH = "/content";

    String DOWNLOAD_PATH = "/v1/f";

    String guessContentType(String pathname);

    String write(String pathname, InputStream is, String contentType) throws FileException;

    default String write(String pathname, InputStream is) throws FileException {
        return write(pathname, is, null);
    }

    default void deleteFilesQuietly(List<String> pathnames) {
        if(!CollectionUtils.isEmpty(pathnames)){
            pathnames.forEach(e->{
                deleteFileQuietly(e);
            });
        }
    }

    default void deleteFileQuietly(String pathname) {
        if (Strings.isNotBlank(pathname)) {
            try {
                deleteFile(pathname);
            } catch (Exception e) {
                //ignore exception
            }
        }
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

    String upload(String pathname, InputStream is, boolean overwrite);

    /**
     * upload one file within the request
     * return the first file pathname if multiple files found
     */
    String upload(MultipartHttpServletRequest request, String folder, boolean overwrite);

    default String upload(MultipartHttpServletRequest request, String folder) {
        return upload(request, folder, false);
    }

    /**
     *
     * @param pathname
     * @return inputStream contentType
     * @throws FileException
     */
    InputStream get(String pathname) throws FileException;

    List<String> getUrl(List<String> pathnames, boolean download);

    default List<String> getUrl(List<String> pathnames) {
        return getUrl(pathnames, false);
    }

    default String getUrl(String pathname) {
        pathname = FileUtils.getFileUri(pathname);
        List<String> urls = getUrl(Collections.singletonList(pathname), false);
        return (CollectionUtils.isEmpty(urls) ? null : urls.get(0));
    }

    /**
     * 获取当前目录下的所有文件地址
     */
    List<String> listObjects(String folderPath);

    /**
     * TODO 如何做到三种文件存储，返回统一的文件信息
     * 用来获取文件的信息如文件名称、大小、操作时间等
     * local: getFile(pathname) and return class java.io.File
     * minio: statObject(bucketName,keyName) and return class io.minio.ObjectStat;
     * oss: getObjectMetadata(bucketName,keyName) and return class com.aliyun.oss.model.ObjectMetadata;
     */
    File getFile(String fileUrl);

    String copyFile(String oldFilePath, String newFilePath, boolean overwrite);

}