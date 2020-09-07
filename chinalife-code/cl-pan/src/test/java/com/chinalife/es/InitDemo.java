package com.chinalife.es;

import com.chinalife.ClPanApplication;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.common.utils.IdWorker;
import com.chinalife.pan.dao.HadoopDao;
import com.chinalife.pan.po.PanFile;
import com.chinalife.pan.repository.PanFileRepository;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.spring.annotation.MapperScan;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ClPanApplication.class})
@MapperScan("com.chinalife.es")
public class InitDemo {
    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private PanFileRepository panFileRepository;

    @Autowired
    private ClerkMapper mapper;

    @Autowired
    private HadoopDao hadoopDao;

    @Autowired
    private IdWorker idWorker;

    public InitDemo() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

    @Test
    public void testInit() {
        template.createIndex(PanFile.class);
        template.putMapping(PanFile.class);
        PanFile panFile = new PanFile(Long.toString(idWorker.nextId()), "/", null, "/", true, new Date(), null, null);
        panFileRepository.save(panFile);
        List<Clerk> clerks = mapper.selectAll();
        for (Clerk clerk : clerks) {
            panFile = new PanFile(Long.toString(idWorker.nextId()), "/" + clerk.getId(),
                    "/", clerk.getId(), true, new Date(), null, null);
            hadoopDao.mkDir(panFile.getPathName());
            panFileRepository.save(panFile);
        }
    }

    @Test
    public void testSearch() {
        String str = "/17003004/";
        String searchStr = QueryParser.escape(str);
        List<PanFile> ans = panFileRepository.findPanFilesByPathNameStartsWith(searchStr);
        for (PanFile an : ans) {
            System.out.println(an);
        }
    }

    @Test
    public void testDel() {
        panFileRepository.deleteById("1288723995742572544");
    }

    @Test
    public void testRename() {
        String name = "/17003004/test";
        String newName = "/17003004/test2";
        hadoopDao.rename(name, newName);
    }

}
