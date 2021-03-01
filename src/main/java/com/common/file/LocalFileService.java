package com.common.file;

import com.common.exceptions.FileException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * for dev purpose only, not for production
 */
@Log4j2
public class LocalFileService extends AbstractFileService implements FileService {

    /**
     * root path for file storage
     */
    private String path;

    public LocalFileService(String path) {
        this.path = path;
        log.debug("init local file service with path {}", path);
    }

    @Override
    public String write(String pathname, InputStream is, String contentType) throws FileException {
		try(OutputStream fop = getOutputStream(pathname)) {
			IOUtils.copy(is, fop);
		} catch (IOException e) {
            throw new FileException("error writing file " + pathname, e);
        }      
        return pathname;
    }

	private OutputStream getOutputStream(String filename) throws IOException {
		File file = new File(path , filename);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}

    @Override
    public void deleteFile(String pathname) throws FileException {
        FileUtils.deleteQuietly(new File(path , pathname));
    }

    @Override
    public void deletePath(String pathname) throws FileException {
        try {
            FileUtils.deleteDirectory(new File(path , pathname));
        } catch (IOException e) {
            throw new FileException("failed to delete path " + pathname, e);
        }
    }

    @Override
    public boolean exists(String pathname) throws FileException {
        File file = new File(path , pathname);
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
        File file = new File(path , pathname);
        if (file.exists() && file.isFile()) {
            try {
                return new FileInputStream(file);
            } catch (IOException e) {
                throw new FileException("error reading file " + pathname, e);
            }
        }
        return null;
    }

}