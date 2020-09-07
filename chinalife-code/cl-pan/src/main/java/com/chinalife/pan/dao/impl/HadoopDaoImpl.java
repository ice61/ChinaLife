package com.chinalife.pan.dao.impl;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.pan.config.HadoopProperties;
import com.chinalife.pan.dao.HadoopDao;
import com.chinalife.pan.interceptor.PanFileInterceptor;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.URI;


@Repository
@EnableConfigurationProperties(HadoopProperties.class)
public class HadoopDaoImpl implements HadoopDao {

    @Autowired
    private HadoopProperties hdp;

    @Override
    public void mkDir(String pathName) {
        FileSystem fileSystem = getFileSystem();
        try {
            fileSystem.mkdirs(new Path(pathName));
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public void upload(String localPath, String hdfsPath) {
        FileSystem fileSystem = getFileSystem();
        try {
            fileSystem.copyFromLocalFile(new Path(localPath), new Path(hdfsPath));
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }

    @Override
    public String download(String localPath, String hdfsPathName) {
        FileSystem fileSystem = getFileSystem();
        try {
            String fileName = hdfsPathName.substring(
                    hdfsPathName.lastIndexOf("/") + 1, hdfsPathName.length());
            fileName = localPath + fileName;
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            FSDataInputStream inputStream = fileSystem.open(new Path(hdfsPathName));
            IOUtils.copyBytes(inputStream, outputStream, 1024);
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
            return fileName;
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.DOWNLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String pathName) {
        FileSystem fileSystem = getFileSystem();
        try {
            fileSystem.delete(new Path(pathName), true);
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.DELETE_FILE_ERROR);
        }
    }

    @Override
    public void rename(String oldPathName, String newPathName) {
        FileSystem fileSystem = getFileSystem();
        try {
            fileSystem.rename(new Path(oldPathName), new Path(newPathName));
        } catch (IOException e) {
            throw new ClException(ExceptionEnum.RENAME_FILE_ERROR);
        }
    }

    private FileSystem getFileSystem() {
        Configuration configuration = new Configuration();
        try {
            FileSystem fileSystem = FileSystem.get(new URI(hdp.getPath()), configuration, hdp.getUser());
            return fileSystem;
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
