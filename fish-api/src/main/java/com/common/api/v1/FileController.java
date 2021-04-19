package com.common.api.v1;

import com.common.file.FileService;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.common.file.FileService.*;

@Controller
@RequestMapping(CONTROLLER_PATH)
public class FileController {

    private static final String CONTENT_HEADER = "Content-Disposition";

    private static final String UTF_8 = "UTF-8";

    private static final String CONTENT_PREFIX = CONTROLLER_PATH + CONTENT_PATH + "/";

    private static final String DOWNLOAD_PREFIX = CONTROLLER_PATH + DOWNLOAD_PATH + "/";

    private static List<String> ieAgents = ImmutableList.of("MSIE", "Trident", "Edge");

    @Autowired
    private FileService fileService;

    @GetMapping(CONTENT_PATH + "/**")
    public void getContent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        getFile(request, response, false);
    }

    @GetMapping(DOWNLOAD_PATH + "/**")
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        getFile(request, response, true);
    }

    private void getFile(HttpServletRequest request, HttpServletResponse response, boolean download) throws Exception {
        String path = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        String pathname = StringUtils.removeStart(path, (download ? DOWNLOAD_PREFIX : CONTENT_PREFIX));

        try (InputStream is = fileService.get(pathname)) {
            if (is == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
    
            response.setCharacterEncoding(UTF_8);
            setHeader(request, response, pathname, download);
            IOUtils.copy(is, response.getOutputStream());
        }
    }

    private void setHeader(HttpServletRequest request, HttpServletResponse response, String pathname, boolean download)
            throws Exception {

        String contentType = fileService.guessContentType(pathname);
        if (contentType != null) {
            response.setContentType(contentType);
        }

        String filename = FilenameUtils.getName(pathname);
        String content = null;
        if (download) {
            content = "attachment;filename=\"" + getFileName(request, filename) + "\"";
        } else {
            content = "filename=" + URLEncoder.encode(filename, UTF_8);
        }
        response.setHeader(CONTENT_HEADER, content);
    }

    private static String getFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        return (isIE(request) ? URLEncoder.encode(fileName, UTF_8) : new String(fileName.getBytes(UTF_8), "ISO-8859-1"));
    }

    private static boolean isIE(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent");
        return ieAgents.contains(agent);
    }

}