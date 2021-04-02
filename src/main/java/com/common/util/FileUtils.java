package com.common.util;

import com.common.exceptions.RestApiException;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	private static final char FILE_SEPERATOR_NIX = '/';
	private static final char FILE_SEPERATOR_WIN = '\\';
	private static final String FILE_LOCAL = "local://";
	private static final String FILE_HTTP = "http://";
	private static final String FILE_HTTPS = "https://";
	public static String ROOT_PATH = "";
    public static String LABEL_NODEFILE_URL="";

	public static void writeFile(InputStream content, String filename) throws IOException {
		OutputStream fop = null;
		try {
			fop = getOutputStream(filename); 
			IOUtils.copy(content, fop);
		} finally {
			IOUtils.closeQuietly(fop);
		}
	}

	/**
	 * touchFolders("e-100/accountdata/20151105/1.xlsx")
	 * 
	 * touch folder e-100, accountdata, 20151105 in order.
	 */
	public static boolean touchFolders(String filename) {
		if (StringUtils.isBlank(filename)) {
			return false;
		}
		
		String[] folders = StringUtils.split(filename, File.separator);
		String folder = ROOT_PATH;
		for (int i = 0; i < folders.length; i++) {
			if (i != 0) {
				folder += File.separator;
			}
			folder += folders[i];
			File f = new File(folder);
			if (f.isDirectory()) {
				if (!touch(f)) {
					return false;
				}
				logger.debug("touched folder {}", folder);
			}
		}
		
		return true;
	}
	
    public static boolean touch(File file) {
        if (!file.exists()) {
        	return false;
        }
        return file.setLastModified(System.currentTimeMillis());
    }
	
	public static OutputStream getOutputStream(String filename) throws IOException {
		File file = new File(ROOT_PATH , filename);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}
	
	//local file url is like,  local://e-1/640dae40-9e3a-4699-bdeb-db5e66ffab6f.jpg
	public static String toLocalFileUrl(String filename) {
		if (isLocalFileUrl(filename)) {
			return filename;
		} else {
			return FILE_LOCAL + filename.replace(FILE_SEPERATOR_WIN, FILE_SEPERATOR_NIX);
		}
	}
	
    public static String getFileUri(String localFileUrl) {
        if (StringUtils.isBlank(localFileUrl)) {
            return null;
        }
        if (localFileUrl.startsWith(FILE_LOCAL)) {
			return subFileLocalString(localFileUrl);
		}
        return localFileUrl;
    }

	private static String subFileLocalString(String url){
		if (Strings.isBlank(url) || !url.toLowerCase().startsWith(FILE_LOCAL)){
			return url;
		}
		return url.substring(FILE_LOCAL.length());
	}
    
    public static String getDownLoadFileUri(String contextPath, String localFileUrl) {
    	return getDownLoadFileUri(contextPath, localFileUrl, false);
    }
    
    public static String getDownLoadFileUri(String contextPath,String localFileUrl, boolean attachment) {
    	if (StringUtils.isBlank(localFileUrl)) {
            return null;
        } else {
    		return LABEL_NODEFILE_URL + contextPath + "/download/labelfile.do?id=" + localFileUrl + (attachment ? "&attachment=true" : "");
    	}
    }
    
    public static String transformFileUrl(String contextPath, String fileUrl) {
    	return transformFileUrl(contextPath, fileUrl, false);
    }
    
    public static String transformFileUrl(String contextPath, String fileUrl, boolean attachment) {
    	if (StringUtils.isBlank(fileUrl)) {
    		return null;
    	}
    	if (isLocalFileUrl(fileUrl)) {
    		return getDownLoadFileUri(contextPath, fileUrl, attachment);
    	} else {
    		return fileUrl;
    	}
    }
    
	private static String getLocalFilePath(String localFileUrl) {
		localFileUrl = subFileLocalString(localFileUrl);
        if (Strings.isNotBlank(localFileUrl) && File.separatorChar != FILE_SEPERATOR_NIX) {
            localFileUrl = localFileUrl.replace(FILE_SEPERATOR_NIX, File.separatorChar);
        }
        return FilenameUtils.normalize(localFileUrl);
	}

    //local://e-1/640dae40-9e3a-4699-bdeb-db5e66ffab6f.jpg or e-1/640dae40-9e3a-4699-bdeb-db5e66ffab6f.jpg
	public static File getLocalFile(String localFileUrl) {
		localFileUrl = getLocalFilePath(localFileUrl);
        return new File(ROOT_PATH , localFileUrl);
	}

	public static File getLocalFile(String rootPath,String localFileUrl) {
		if (Strings.isNotBlank(rootPath)){
			localFileUrl = getLocalFilePath(localFileUrl);
			return new File(rootPath , localFileUrl);
		} else {
			return getLocalFile(localFileUrl);
		}
	}

	public static void deleteLocalFileQuitely(String localFileUrl) {
		if (StringUtils.isBlank(localFileUrl)) {
			return;
		}
		try {
			File f = getLocalFile(localFileUrl);
			if (f != null && f.isFile()) {	// check isfile for safety
				if (!f.delete()) {
					f.deleteOnExit();
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}
	
	public static String extractExt(String fileName) {
		if (StringUtils.isBlank(fileName))
			return "";
		int i = fileName.lastIndexOf(FILE_SEPERATOR_WIN);
		if (i > -1) {
			fileName = fileName.substring(i + 1);
		}
		i = fileName.lastIndexOf(FILE_SEPERATOR_NIX);
		if (i > -1) {
			fileName = fileName.substring(i + 1);
		}
		i = fileName.lastIndexOf('.');
		if (i > -1) {
			return fileName.substring(i + 1);
		}
		return fileName;
	}

	public static boolean isValidFileUrl(String fileUrl) {
		boolean result = false;
		if (StringUtils.isNotBlank(fileUrl)) {
			String lowerCase = fileUrl.toLowerCase();
			if (lowerCase.startsWith(FILE_LOCAL) || lowerCase.startsWith(FILE_HTTP) || lowerCase.startsWith(FILE_HTTPS)) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * <p>检查请求{@request}中的上传文件名称。如果出现不一致，则抛出异常</p>
	 * <p></p>
	 * <p>检查项：</p>
	 * <p>1.是否为约定的上传参数后缀{@fileNameSuffix}</p>
	 * <p>2.是否为允许上传的文件参数名称，需要从指定的文件参数名称集合{@specifiedFields}中逐个对比</p>
	 * @param request 请求
	 * @param specifiedFields 指定的文件名称集合
	 * @param fileNameSuffix 约定的上传参数后缀
	 * */
	public static void validateFiledNames(MultipartHttpServletRequest request, List<String> specifiedFields, String fileNameSuffix) throws RestApiException {
		List<String> errorFieldNames = Lists.newArrayList(request.getFileNames())
				.stream()
				//如果文件不是以规定的_file结尾，或者文件名称不是企业信息里的字段
				.filter(fileName -> !fileName.endsWith(fileNameSuffix)||!specifiedFields.contains(fileName.split(fileNameSuffix)[0]))
				.collect(Collectors.toList());

		if (errorFieldNames.size()>0){
		//	throw new RestApiException(ApiError.ENT_UPDATE_FILNAME_NOT_AUTH,errorFieldNames);
		}
	}

	public static boolean isLocalFileUrl(String fileUrl) {
        boolean result = false;
        if (StringUtils.isNotBlank(fileUrl)) {
            String lowerCase = fileUrl.toLowerCase();
            if (lowerCase.startsWith(FILE_LOCAL)) {
                result = true;
            }
        }
        return result;
    }
	
	public static String saveBase64File(String pathname, String base64Str) throws IOException {
    	if (StringUtils.isBlank(base64Str)) {
    		return null;
    	}
    	byte[] data = Base64.decodeBase64(base64Str.getBytes());
    	writeFile(new ByteArrayInputStream(data), pathname);
    	return FileUtils.toLocalFileUrl(pathname);
	}

	public static String lowercaseExt(String filename) {
		return changeExtCase(filename, true);
	}
	
	private static String changeExtCase(String filename, boolean lowercase) {
		String ext = FilenameUtils.getExtension(filename);
		if (StringUtils.isBlank(ext)) {
			return filename;
		}
		
		String name = StringUtils.removeEnd(filename, ext);
		ext = (lowercase ? ext.toLowerCase() : ext.toUpperCase());
		return (name + ext);
	}

	public static String getEnterpriseDirPath(Long enterpriseId) {
		return (enterpriseId == null ? "e-enterprise" : "e-" + enterpriseId+File.separator+"enterprise");
	}
	
	/**
	 * parseEntId("local://e-3/enterprise/d5763aba-589a-4ee5-8ab8-124ca0946c78.png") == "3"
	 */
	public static Long parseEntId(String localFileUrl) {
		if (!isLocalFileUrl(localFileUrl)) {
			return null;
		}
		String s = StringUtils.removeStart(localFileUrl, FILE_LOCAL + "e-");
		s = s.substring(0, s.indexOf('/'));
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static void setBeanFields(Object bean, Map<String, String> uploadFields,String fileNameSuffix) {
		if (!Objects.isNull(uploadFields) && uploadFields.size()>0){
			uploadFields.forEach((k,v) ->
					BeanUtil.setPropertyQuietly(bean,k.split(fileNameSuffix)[0],v)
			);
		}
	}

	public static String decodeStringQuietly(String value,String charset){
		if (Strings.isBlank(value)){
			return null;
		}

		charset = Strings.isBlank(charset) ? "utf-8" : charset;
		try {
			value= URLDecoder.decode(value, charset);
		} catch (Exception e) {
			//ignore
		}
		return value;
	}

	public static String decodeStringUTF8Quietly(String value){
		return decodeStringQuietly(value,"utf-8");
	}


	public static void main(String[] args) throws IOException {
//		FileUtils.writeFile(new byte[]{65,65,67,68}, "/label-files/aa/bb/cc.txt");
		System.out.println(FileUtils.extractExt("abcd.png"));
		System.out.println(FileUtils.extractExt("jpng"));
		System.out.println(FileUtils.extractExt("/asdf/aa.jpag"));
		System.out.println(FileUtils.extractExt("/asdf/jpeg"));
		System.out.println(FileUtils.extractExt("\\asdf\\jaa.peg"));
		System.out.println(FileUtils.extractExt("\\asdf\\japeg"));
		System.out.println(FileUtils.getLocalFile("local://e-1/640dae40-9e3a-4699-bdeb-db5e66ffab6f.jpg").getCanonicalPath());
	}
}
