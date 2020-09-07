package com.chinalife.pan.repository;

import com.chinalife.pan.po.PanFile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PanFileRepository extends ElasticsearchRepository<PanFile, String> {

    PanFile findPanFileByPathName(String pathName);

    List<PanFile> findPanFilesByParent(String parent);

    List<PanFile> findPanFilesByPathNameStartsWith(String pathName);

}
