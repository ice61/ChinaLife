package com.chinalife.upload.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UploadController {

    ResponseEntity<String> uploadImage(MultipartFile file);

}
