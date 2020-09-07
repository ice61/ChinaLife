package com.chinalife.upload.service.impl;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.upload.config.UploadProperties;
import com.chinalife.upload.service.UploadService;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadServiceImpl implements UploadService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private UploadProperties prop;
    @Autowired
    private UploadService uploadService;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (!prop.getAllowTypes().contains(contentType)) {
                throw new ClException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new ClException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            String entension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), entension, null);
            return prop.getBaseUrl() + storePath.getFullPath();

        } catch (IOException e) {
            log.error("文件上传失败",e);
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
