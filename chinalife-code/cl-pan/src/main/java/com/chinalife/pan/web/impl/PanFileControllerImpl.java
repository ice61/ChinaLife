package com.chinalife.pan.web.impl;

import com.chinalife.pan.service.PanFileService;
import com.chinalife.pan.vo.PanFilePageResult;
import com.chinalife.pan.web.PanFileController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
public class PanFileControllerImpl implements PanFileController {

    @Autowired
    private PanFileService panFileService;

    @Override
    @GetMapping("/browse")
    public ResponseEntity<PanFilePageResult> listFiles(
            @RequestParam(value = "directorName") String directorName,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc) {
        return ResponseEntity.ok(panFileService.listFiles(directorName, page, rows, sortBy, desc));
    }

    @Override
    @PostMapping("/mkdir")
    public ResponseEntity<Void> mkDir(
            @RequestParam("path") String path,
            @RequestParam("name") String name) {
        panFileService.mkDir(path, name);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @GetMapping("/searchfiles")
    public ResponseEntity<PanFilePageResult> searchFiles(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc) {
        return ResponseEntity.ok(panFileService.searchFiles(key, page, rows, sortBy, desc));
    }

    @Override
    @PostMapping("/upload")
    public ResponseEntity<Void> upload(
            @RequestParam("path") String path,
            @RequestParam("file") MultipartFile file) {
        panFileService.upload(path, file);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/download")
    public void download(
            @RequestParam("pathName") String pathName,
            HttpServletResponse response) {
        panFileService.download(pathName, response);
    }

    @Override
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam("pathName") String pathName) {
        panFileService.delete(pathName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    @PostMapping("/rename")
    public ResponseEntity<Void> rename(
            @RequestParam("oldPathName") String oldPathName,
            @RequestParam("newName") String newName) {
        panFileService.rename(oldPathName, newName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
