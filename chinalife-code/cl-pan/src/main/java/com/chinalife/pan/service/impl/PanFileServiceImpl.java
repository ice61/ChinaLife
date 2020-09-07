package com.chinalife.pan.service.impl;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.IdWorker;
import com.chinalife.pan.dao.HadoopDao;
import com.chinalife.pan.interceptor.PanFileInterceptor;
import com.chinalife.pan.po.PanFile;
import com.chinalife.pan.repository.PanFileRepository;
import com.chinalife.pan.service.PanFileService;
import com.chinalife.pan.vo.PanFilePageResult;
import io.lettuce.core.resource.DirContextDnsResolver;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.jets3t.service.io.TempFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.lucene.queryparser.classic.QueryParser;
import sun.awt.SunGraphicsCallback;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PanFileServiceImpl implements PanFileService {

    @Autowired
    private PanFileRepository panFileRepository;

    @Autowired
    private HadoopDao hadoopDao;

    @Autowired
    private IdWorker idWorker;

    private static final String UPLOAD_TEMP_PATH = "F:\\store\\upload\\";

    private static final String DOWNLOAD_TEMP_PATH = "F:\\store\\download\\";

    @Override
    public PanFilePageResult listFiles(String directorName, Integer page, Integer rows, String sortBy, Boolean desc) {
        directorName = processPath(directorName);
        if (!directoryIsLegal(directorName)) {
            throw new ClException(ExceptionEnum.INVALID_FILE_DIRECTORY);
        }
        page = page <= 0 ? 1 : page;
        rows = rows <= 0 ? 5 : rows;
        page--;
        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(null, new String[]{"id", "parent", "md5", "content"}));
        // 分页
        queryBuilder.withPageable(PageRequest.of(page, rows));
        //根据parent字段查询
        queryBuilder.withQuery(QueryBuilders.termQuery("parent", directorName));
        // 排序
        if (!StringUtils.isBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        Page<PanFile> result = panFileRepository.search(queryBuilder.build());
        processResult("/" + PanFileInterceptor.getId(), result);
        PanFilePageResult panFilePageResult = new PanFilePageResult();
        panFilePageResult.setData(result.getContent());
        panFilePageResult.setTotal(result.getTotalElements());
        panFilePageResult.setTotalPage(result.getTotalPages());
        return panFilePageResult;
    }

    @Override
    public void mkDir(String path, String name) {
        if (name.contains("/"))
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        path = processPath(path);
        if (!directoryIsLegal(path)) {
            throw new ClException(ExceptionEnum.INVALID_FILE_DIRECTORY);
        }
        String pathName = null;
        if (path.equals("/")) {
            pathName = path + name;
        } else {
            pathName = path + "/" + name;
        }
        if (fileIsExist(pathName)) {
            throw new ClException(ExceptionEnum.DIRECTORY_HAS_EXIST);
        }
        hadoopDao.mkDir(pathName);
        PanFile panFile = new PanFile(Long.toString(idWorker.nextId()), pathName, path, name, true, new Date(), null, null);
        panFileRepository.save(panFile);
    }

    @Override
    public PanFilePageResult searchFiles(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        String path = "/";
        Boolean ans = PanFileInterceptor.getAuth();
        if (ans == false) {
            path = path + PanFileInterceptor.getId();
        }
        page = page <= 0 ? 1 : page;
        rows = rows <= 0 ? 5 : rows;
        page--;
        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(null, new String[]{"id", "parent", "md5", "content"}));
        // 分页
        queryBuilder.withPageable(PageRequest.of(page, rows));
        //在权限范围内对所有文件进行检索（过滤掉用户的根目录）
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.prefixQuery("pathName", path));
        boolQueryBuilder.mustNot(QueryBuilders.termQuery("pathName", path));
        //根据key进行搜索
        BoolQueryBuilder boolQueryBuilderKey = new BoolQueryBuilder();
        boolQueryBuilderKey.should(QueryBuilders.matchQuery("name", key));
        boolQueryBuilderKey.should(QueryBuilders.matchQuery("content", key));
        //结合进总的boolQuery
        boolQueryBuilder.must(boolQueryBuilderKey);
        //结合
        queryBuilder.withQuery(boolQueryBuilder);
        // 排序
        if (!StringUtils.isBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        Page<PanFile> result = panFileRepository.search(queryBuilder.build());
        processResult(path, result);
        PanFilePageResult panFilePageResult = new PanFilePageResult();
        panFilePageResult.setData(result.getContent());
        panFilePageResult.setTotal(result.getTotalElements());
        panFilePageResult.setTotalPage(result.getTotalPages());
        return panFilePageResult;
    }

    @Override
    public void upload(String path, MultipartFile file) {
        path = processPath(path);
        if (!directoryIsLegal(path)) {
            throw new ClException(ExceptionEnum.INVALID_FILE_DIRECTORY);
        }
        if (file.getSize() == 0) {
            throw new ClException(ExceptionEnum.EMPTY_FILE);
        }
        if (file.getOriginalFilename().contains("/")) {
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        }
        String pathName = path + "/" + file.getOriginalFilename();
        //先将文件存至本地
        String tempFileName = copyFileFromVue(file);
        //获取md5值
        String md5 = getMd5(tempFileName);
        //检查文件名是否重复
        String searchPath = QueryParser.escape(pathName);
        PanFile temp = panFileRepository.findPanFileByPathName(searchPath);
        if (temp == null) {
            String content = readWord(tempFileName);
            PanFile panFile = new PanFile(Long.toString(idWorker.nextId()), pathName, path,
                    file.getOriginalFilename(), false, new Date(), md5, content);
            hadoopDao.upload(tempFileName, pathName);
            panFileRepository.save(panFile);
            delFile(tempFileName);
        } else {
            String oldMd5 = temp.getMd5();
            delFile(tempFileName);
            if (md5.equals(oldMd5)) return;
            else throw new ClException(ExceptionEnum.FILE_NAME_HAS_EXIST);
        }
    }

    @Override
    public void download(String pathName, HttpServletResponse response) {
        pathName = processPath(pathName);
        if (!fileIsLegal(pathName)) {
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        }
        String localPathName = copyFileFromHadoop(pathName);
        try {
            FileInputStream inputStream = new FileInputStream(localPathName);
            File file = new File(localPathName);
            response.reset();
            response.setContentType("bin");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            byte[] b = new byte[1024];
            int len;
            while ((len = inputStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            inputStream.close();
            delFile(localPathName);
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.DOWNLOAD_FILE_ERROR);
        }
    }

    @Override
    public void delete(String pathName) {
        if (pathName.equals("/")) {
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        }
        pathName = processPath(pathName);
        if (!fileIsExist(pathName)) {
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        }
        hadoopDao.delete(pathName);
        String searchStr = QueryParser.escape(pathName);
        List<PanFile> ans = panFileRepository.findPanFilesByPathNameStartsWith(searchStr);
        panFileRepository.deleteAll(ans);
    }

    @Override
    public void rename(String oldPathName, String newName) {
        if (newName.contains("/") || oldPathName.equals("/")) {
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        }
        oldPathName = processPath(oldPathName);
        String searchStr = QueryParser.escape(oldPathName);

        //查看要更改的文件是否存在
        PanFile ans = panFileRepository.findPanFileByPathName(searchStr);
        if (ans == null) {
            throw new ClException(ExceptionEnum.INVALID_FILE_NAME);
        }
        //拼接新的全路径名
        String newPathName = null;
        if (ans.getParent().equals("/")) {
            newPathName = "/" + newName;
        } else {
            newPathName = ans.getParent() + "/" + newName;
        }
        //查看新全路径名下是否已经存在文件
        searchStr = QueryParser.escape(newPathName);
        if (panFileRepository.findPanFileByPathName(searchStr) != null) {
            throw new ClException(ExceptionEnum.FILE_NAME_HAS_EXIST);
        }
        //使用hadoopDao直接对hdfs进行递归改名
        hadoopDao.rename(oldPathName, newPathName);
        //手动维护es,如果待改名的是文件夹，需要递归改名
        if (ans.getIsdirectory()) {
            dfsRePathName(oldPathName, newPathName);
        }
        ans.setPathName(newPathName);
        ans.setName(newName);
        panFileRepository.save(ans);
    }

    void dfsRePathName(String parent, String newParent) {
        String newPathName = null;
        String searchParent = QueryParser.escape(parent);
        List<PanFile> ans = panFileRepository.findPanFilesByParent(searchParent);
        for (PanFile panFile : ans) {
            if (newParent.equals("/")) {
                newPathName = "/" + panFile.getName();
            } else {
                newPathName = newParent + "/" + panFile.getName();
            }
            if (panFile.getIsdirectory()) {
                dfsRePathName(panFile.getPathName(), newPathName);
            }
            panFile.setPathName(newPathName);
            panFile.setParent(newParent);
            panFileRepository.save(panFile);
        }
    }

    private String copyFileFromHadoop(String pathName) {
        String localPath = DOWNLOAD_TEMP_PATH + PanFileInterceptor.getId() + File.separator;
        File tempDir = new File(localPath);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        String localPathName = hadoopDao.download(localPath, pathName);
        return localPathName;
    }

    //将前端上传的文件暂存至本地
    private String copyFileFromVue(MultipartFile file) {
        OutputStream outputStream = null;
        InputStream inputStream = null;
        String fileName = null;
        String ans = null;
        try {
            inputStream = file.getInputStream();
            fileName = file.getOriginalFilename();
            byte[] bs = new byte[1024];
            int len;
            File tempFile = new File(UPLOAD_TEMP_PATH + PanFileInterceptor.getId() + File.separator);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            outputStream = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            while ((len = inputStream.read(bs)) != -1) {
                outputStream.write(bs, 0, len);
            }
            inputStream.close();
            outputStream.close();
            ans = tempFile.getPath() + File.separator + fileName;
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
        return ans;
    }

    //处理查询结果，删除不必要的根目录，提高用户体验
    private void processResult(String path, Page<PanFile> result) {
        if (PanFileInterceptor.getAuth() == false) {
            for (PanFile panFile : result) {
                panFile.setPathName(panFile.getPathName().replaceFirst(path, ""));
            }
        }
    }

    //文件存在并且类型是文件夹 => 合法
    private boolean directoryIsLegal(String path) {
        String searchPath = QueryParser.escape(path);
        PanFile temp = panFileRepository.findPanFileByPathName(searchPath);
        if (temp == null || temp.getIsdirectory() == false) {
            return false;
        }
        return true;
    }

    private boolean fileIsExist(String pathName) {
        String searchPath = QueryParser.escape(pathName);
        PanFile temp = panFileRepository.findPanFileByPathName(searchPath);
        if (temp != null) {
            return true;
        }
        return false;
    }

    //文件存在并且类型时文件 => 合法
    private boolean fileIsLegal(String pathName) {
        String searchPath = QueryParser.escape(pathName);
        PanFile temp = panFileRepository.findPanFileByPathName(searchPath);
        if (temp == null || temp.getIsdirectory() == true) {
            return false;
        }
        return true;
    }

    private String processPath(String directoryName) {
        String id = PanFileInterceptor.getId();
        Boolean ans = PanFileInterceptor.getAuth();
        //ans为true表示此用户拥有管理网盘的权限
        if (ans == false) {
            if (directoryName.equals("/"))
                directoryName += id;
            else
                directoryName = "/" + id + directoryName;
        }
        return directoryName;
    }

    //删除本地文件
    private void delFile(String pathName) {
        File file = new File(pathName);
        file.delete();
    }

    //获取本地文件的md5值
    private String getMd5(String pathName) {
        FileInputStream fis = null;
        String md5 = null;
        try {
            fis = new FileInputStream(pathName);
            md5 = DigestUtils.md5Hex(IOUtils.toByteArray(fis));
            IOUtils.closeQuietly(fis);
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
        return md5;
    }

    //将本地word文件转为字符串
    private String readWord(String pathName) {
        String buffer = null;
        try {
            if (pathName.endsWith(".doc")) {
                FileInputStream is = new FileInputStream(pathName);
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText();
                is.close();
            } else if (pathName.endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(pathName);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                opcPackage.close();
            }
        } catch (Exception e) {
            throw new ClException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
        return buffer;
    }
}
