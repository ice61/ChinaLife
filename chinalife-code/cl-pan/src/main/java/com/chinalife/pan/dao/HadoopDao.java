package com.chinalife.pan.dao;

import java.io.FileNotFoundException;

public interface HadoopDao {

    void mkDir(String pathName);

    void upload(String localPath, String hdfsPath);

    String download(String localPath, String hdfsPath);

    void delete(String pathName);

    void rename(String oldPathName,String newPathName);
}
