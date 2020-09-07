package com.chinalife.pan.service;

import com.chinalife.pan.vo.PanFilePageResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface PanFileService {
    PanFilePageResult listFiles(String directorName, Integer page, Integer rows, String sortBy, Boolean desc);

    void mkDir(String path, String name);

    PanFilePageResult searchFiles(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    void upload(String path, MultipartFile file);

    void download(String pathName, HttpServletResponse response);

    void delete(String pathName);

    void rename(String oldPathName, String newName);
}
