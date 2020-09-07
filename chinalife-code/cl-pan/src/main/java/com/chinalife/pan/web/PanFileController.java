package com.chinalife.pan.web;

import com.chinalife.pan.vo.PanFilePageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface PanFileController {
    ResponseEntity<PanFilePageResult> listFiles(String directorName, Integer page, Integer rows, String sortBy, Boolean desc);

    ResponseEntity<Void> mkDir(String path, String name);

    ResponseEntity<PanFilePageResult> searchFiles(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    ResponseEntity<Void> upload(String path, MultipartFile file);

    void download(String pathName, HttpServletResponse response);

    ResponseEntity<Void> delete(String pathName);

    ResponseEntity<Void> rename(String oldPathName, String newName);

}
