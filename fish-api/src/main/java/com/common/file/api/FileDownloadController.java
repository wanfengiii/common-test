package com.common.file.api;


import com.common.file.FileService;
import com.common.util.ImageUtils;
import com.google.common.collect.ImmutableList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

@Controller
@Api(tags="fileService文件导出")
@Log4j2
public class FileDownloadController {

    private static final String CONTENT_HEADER = "Content-Disposition";

    private static final String UTF_8 = "UTF-8";

    private static List<String> ieAgents = ImmutableList.of("MSIE", "Trident", "Edge");

    private static final String ROOT = "/v1/f";

    @Autowired
    private FileService fileService;

    //http://localhost:9000/v1/f/trace-data/2021-03-10/%E6%86%A8%E6%86%A81.xls?signature=%2BFdeBycBPJu7Cyepc2U4YSGxrbH8HFbWZMCy3%2Bnvja5NYznxKlGprR5N14nRfGQx&fileName=憨憨22
    //http://localhost:9000/v1/f/trace-data/2021-03-10/XX.png?signature=%2BFdeBycBPJusMlIVbGCZjbUlBcIobZfev7tAv8KJQpw%3D&attachment=false
    @GetMapping(ROOT + "/**")
    public void getFile(HttpServletRequest request, HttpServletResponse response,
                        @RequestParam String signature,
                        @RequestParam(required = false) String fileName,
                        @ApiParam("缩略图宽度")@RequestParam(defaultValue = "0")int width,
                        @ApiParam("缩略图高度")@RequestParam(defaultValue = "0") int height,
                        @RequestParam(required = false, defaultValue = "false") boolean attachment) throws Exception {
        String path = decodeUrl(request, signature);
        doGet(request, response, path, attachment, fileName,width,height);
    }

    /*
      url中文字符处理
     */
    private String decodeUrl(HttpServletRequest request,String signature) throws Exception {
        String uri = request.getRequestURI();
        uri = StringUtils.removeStart(uri,ROOT);
        uri =  uri + "?signature=" + signature;
        return URLDecoder.decode(uri, "UTF-8");
    }

    private void doGet(HttpServletRequest request, HttpServletResponse response, String path,boolean attachment,
                       String fileName,int width,int height)throws Exception{
        try (InputStream is = fileService.get(path)){
            if(is == null){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            path = path.substring(0,path.indexOf("?"));
            String contentType = getContentType(path);
            response.setCharacterEncoding(UTF_8);
            response.setStatus(HttpServletResponse.SC_OK);
            setHeader(request, response, path,contentType,attachment,fileName);

            if (width > 0 && height > 0) {
                //使用一个新的文件流计算出字节数组，然后用文件流+字节数组，得到缩略图的文件流
                byte[] bytes = IOUtils.toByteArray(is);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                try (InputStream thumbnailIS = ImageUtils.getThumbnailInputStream(inputStream,bytes,width,height)){
                    IOUtils.copy(thumbnailIS, response.getOutputStream());
                }
            } else {
                //输出原图
                IOUtils.copy(is, response.getOutputStream());
            }
        }
    }

    private String getContentType(String path){
        String contentType = fileService.guessContentType(path);
        if(StringUtils.isBlank(contentType)){
            contentType = "multipart/form-data";
        }
        return contentType;
    }

    private void setHeader(HttpServletRequest request, HttpServletResponse response, String pathname, String contentType, boolean attachment, String fileName) throws Exception {
        response.setContentType(contentType);
        if(!attachment){
            return;
        }
        String name = getFileName(pathname);
        if(StringUtils.isBlank(fileName)) {
            fileName = name;
        }else{
            String ext = FilenameUtils.getExtension(fileName);
            if(StringUtils.isBlank(ext)) {
                ext = FilenameUtils.getExtension(name);
                fileName += "." + ext.toLowerCase();
            }
        }
        String content  = "attachment;filename=\"" + getFileName(request, fileName) + "\"";
        response.setHeader(CONTENT_HEADER, content);
    }

    private String getFileName(String pathname){
        return pathname.substring(pathname.lastIndexOf("/" ) + 1);
    }

    private  String getFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        return (isIE(request) ? URLEncoder.encode(fileName, UTF_8) : new String(fileName.getBytes(UTF_8), "ISO-8859-1"));
    }

    private  boolean isIE(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        return ieAgents.contains(agent);
    }
}
