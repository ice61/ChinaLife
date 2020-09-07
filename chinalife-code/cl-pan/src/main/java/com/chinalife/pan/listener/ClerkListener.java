package com.chinalife.pan.listener;

import com.chinalife.common.utils.IdWorker;
import com.chinalife.pan.dao.HadoopDao;
import com.chinalife.pan.po.PanFile;
import com.chinalife.pan.repository.PanFileRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
public class ClerkListener {

    @Autowired
    private PanFileRepository panFileRepository;

    @Autowired
    private HadoopDao hadoopDao;

    @Autowired
    private IdWorker idWorker;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "pan.clerk.register.queue", durable = "true"),
            exchange = @Exchange(name = "cl.pan.exchange", type = ExchangeTypes.TOPIC),
            key = {"clerk.register"}
    ))
    public void listenClerkRegister(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        String newPathName = "/" + id;
        String searchPathName = QueryParser.escape(newPathName);
        PanFile ans = panFileRepository.findPanFileByPathName(searchPathName);
        if (ans != null) {
            return;
        }
        PanFile newPanFile = new PanFile(
                Long.toString(idWorker.nextId()), newPathName,
                "/", id, true, new Date(), null, null);
        hadoopDao.mkDir(newPathName);
        panFileRepository.save(newPanFile);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "pan.clerk.del.queue", durable = "true"),
            exchange = @Exchange(name = "cl.pan.exchange", type = ExchangeTypes.TOPIC),
            key = {"clerk.del"}
    ))
    public void listenClerkDel(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        String pathName = "/" + id;
        String searchPathName = QueryParser.escape(pathName);
        List<PanFile> ans = panFileRepository.findPanFilesByPathNameStartsWith(searchPathName);
        if (CollectionUtils.isEmpty(ans)) {
            return;
        }
        hadoopDao.delete(pathName);
        panFileRepository.deleteAll(ans);
    }
}
