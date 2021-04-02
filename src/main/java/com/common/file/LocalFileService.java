package com.common.file;

import com.common.api.response.ApiError;
import com.common.exceptions.FileException;
import com.common.exceptions.RestApiException;
import com.common.util.DESUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * for dev purpose only, not for production
 */
@Log4j2
public class LocalFileService extends AbstractFileService {

    /**
     * root path for file storage
     */
    private String path;

    private static final String EXPIRES_TIME = "time";

    private static final String FILE_NAME = "name";

    public LocalFileService(String path) {
        this.path = path;
        log.info("init local file service with path {}", path);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        path = FilenameUtils.normalize(path, true);
        path = StringUtils.removeEnd(path,"/");
    }

    @Override
    public String write(String pathname, InputStream is, String contentType) throws FileException {
        try (OutputStream fop = getOutputStream(pathname)) {
            IOUtils.copy(is, fop);
        } catch (IOException e) {
            throw new FileException("error writing file " + pathname, e);
        }
        return pathname;
    }

    private OutputStream getOutputStream(String filename) throws IOException {
        File file = getFile(filename);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return new FileOutputStream(file);
    }

    //todo 移除LOCAL_PATH处理逻辑
    public File getFile(String filename) {
        return new File(path, filename);
    }

    @Override
    public void deleteFile(String pathname) {
        FileUtils.deleteQuietly(getFile(pathname));
    }

    @Override
    public void deletePath(String pathname) throws FileException {
        try {
            FileUtils.deleteDirectory(getFile(pathname));
        } catch (IOException e) {
            throw new FileException("failed to delete path " + pathname, e);
        }
    }

    @Override
    public boolean exists(String pathname) throws FileException {
        File file = getFile(pathname);
        return file.exists();
    }

    @Override
    protected String suggestName(String pathname) {
        if (exists(pathname)) {
            String path = FilenameUtils.getPath(pathname);
            String ext = FilenameUtils.getExtension(pathname);
            pathname = path + FilenameUtils.getBaseName(pathname) + "_1." + ext;
            return suggestName(pathname);
        }
        return pathname;
    }

    @Override
    public InputStream get(String pathname) throws FileException {
        // 检验路径及签名是否正确
        validate(pathname);
        pathname = pathname.substring(0,pathname.indexOf("?"));
        File file = getFile(pathname);
        if (file.exists() && file.isFile()) {
            try {
                return new FileInputStream(file);
            } catch (IOException e) {
                throw new FileException("error reading file " + pathname, e);
            }
        }
        return null;
    }

    /**
     * 参考
     * minio : http://localhost:9000/data/2021-03-02/trace.yml?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minioadmin%2F20210303%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20210303T015714Z&X-Amz-Expires=7200&X-Amz-SignedHeaders=host&X-Amz-Signature=b7c0c5a6a9edfac3a82d7cfa27a5b0381788fdd67cd7c3bc5e520563f686d17f
     * 传入path 前拼上fileService配置参数 IP：host/controllerUrl    like IP：host/controllerUrl/path
     * domain: ip:host
     * download_path: controller path
     * signature: 时间加密做签名
     * @param pathname 文件相对路劲
     *                 eg:/trace-data/2020/aa.xls
     * @return domain + download_path + pathname + signature
     *          eg:localhost:9000/v1/f/trace-data/2021-03-02/11.xls?signature=ffgdfgd?fgdgdf=32
     */
    @Override
    protected String getUrl(String pathname, boolean download) {
        if (StringUtils.isBlank(pathname)) {
            return null;
        }
        try {
            return encryptUrl(domain,pathname, expires);
        } catch (URISyntaxException|IllegalBlockSizeException|BadPaddingException|UnsupportedEncodingException e) {
            throw new FileException("failed to get file " + pathname, e);
        }
    }

    private String encryptUrl(String domain, String pathname, Long expires) throws URISyntaxException,IllegalBlockSizeException, BadPaddingException,UnsupportedEncodingException{
        if(!pathname.startsWith("/")) {
            pathname = "/" + pathname;
        }
        String uri = domain + DOWNLOAD_PATH + pathname;
        String fileName = getFileName(pathname);
        String expiresStr = (System.currentTimeMillis() + expires ) +"";
        Map map = ImmutableMap.of(EXPIRES_TIME, expiresStr,FILE_NAME,fileName);
        String stringSignature = encryptSignature(map);
        return appendUri(uri,"signature=" + stringSignature);
    }

    public  String appendUri(String uri, String appendQuery) throws URISyntaxException {
        URI oldUri = new URI(uri);

        String newQuery = oldUri.getQuery();
        if (newQuery == null) {
            newQuery = appendQuery;
        } else {
            newQuery += "&" + appendQuery;
        }

        URI newUri = new URI(oldUri.getScheme(), oldUri.getAuthority(),
                oldUri.getPath(), newQuery, oldUri.getFragment());

        return newUri.toString();
    }

    private void validate(String url) {
        validatePath(url);
        validateSignature(url);
    }

    /**
     * 校验签名，能正确解析且设定时间不过期
     */
    private void validateSignature(String url) throws RestApiException {
        String fileName = getFileName(url);
        String signatureStr = url.substring(url.lastIndexOf("signature=") + 10);
        // 当前签名包含time，name
        Long time = 0L;
        String name = "";
        try {
            Map m = decryptSignature(signatureStr);
            time = Long.valueOf(m.get(EXPIRES_TIME).toString());
            name = m.get(FILE_NAME).toString();
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RestApiException(ApiError.VALIDATE_NOT_PASS, "签名不正确");
        } catch (IllegalArgumentException e) {
            log.info("图片链接校验失败，图片地址：{}",url);
        }

        if (System.currentTimeMillis() > time) {
            throw new RestApiException(ApiError.VALIDATE_NOT_PASS, "文件过期");
        }
        if(!fileName.equals(name)){
            throw new RestApiException(ApiError.VALIDATE_NOT_PASS, "签名内容与访问路劲不匹配");
        }
    }

    private String getFileName(String url){
        String uri = url;
        if(url.contains("signature")) {
            uri = url.substring(0, url.indexOf("?"));
        }
        return uri.substring(uri.lastIndexOf("/" ) + 1);
    }

    /**
     * 校验uri是否在配置的根目录下
     * @param url
     */
    private void validatePath(String url) {
        // 拼接根目录 处理../ 判断是否在还在根目录下
        String uri = path + url.substring(0, url.indexOf("?"));
        uri = FilenameUtils.normalize(uri, true);
        if (!uri.startsWith(path)) {
            throw new RestApiException(ApiError.VALIDATE_NOT_PASS, "路径不合法");
        }
    }

    private String encryptSignature(Map params) throws IllegalBlockSizeException, BadPaddingException,UnsupportedEncodingException{
        String signatureStr = paramToString(params);
        signatureStr =  DESUtil.encrypt(secretKey, signatureStr);
        return URLEncoder.encode(signatureStr,"UTF-8");
    }

    private String paramToString(Map params){
        try {
            return objectMapper.writeValueAsString(params);
        }catch (JsonProcessingException e){
            log.error("加签失败",e);
            throw new RuntimeException();
        }
    }

    private Map decryptSignature(String signatureStr) throws  IllegalBlockSizeException, BadPaddingException{
        signatureStr = DESUtil.decrypt(secretKey, signatureStr);
        try {
            return objectMapper.readValue(signatureStr,Map.class);
        }catch (JsonProcessingException e){
            log.error("解签失败",e);
            throw new RuntimeException();
        }
    }

    @Override
    public String copyFile(String sourcePath, String targetPath, boolean overwrite) {
        if (Strings.isBlank(sourcePath)) {
            return null;
        }
        File oldFile = getFile(sourcePath);
        if (oldFile.exists()) {
            //如果旧文件存在，则准备复制文件
            File newFile = getFile(targetPath);
            if (newFile.exists() && !overwrite) {
                //如果新文件已存在，并且不能覆盖原文件，则在新文件的名称后拼接_1
                suggestName(targetPath);
            }
            //开始复制文件
            try (InputStream fis = new FileInputStream(oldFile)) {
                write(targetPath, fis);
                return targetPath;
            } catch (IOException e) {
                //ignore
            }
        }
        return null;
    }

    @Override
    public List<String> listObjects(String folderPath) {
        List<String> list = new ArrayList<>();

        if (Strings.isBlank(folderPath)) {
            return list;
        }

        File oldFile = getFile(folderPath);
        if (Objects.isNull(oldFile)) {
            return list;
        }

        File[] files = oldFile.listFiles();
        if (Objects.isNull(files) || files.length<1) {
            return list;
        }

        for (File file : files) {
            list.add(file.getPath());
        }
        return list;
    }
}