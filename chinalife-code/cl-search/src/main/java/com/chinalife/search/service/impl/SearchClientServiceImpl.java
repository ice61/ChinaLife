package com.chinalife.search.service.impl;


import com.chinalife.client.po.Client;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.search.client.ClientClient;
import com.chinalife.search.po.SearchClient;
import com.chinalife.search.repository.ClientRepository;
import com.chinalife.search.service.SearchClientService;
import com.chinalife.search.vo.ClientPageResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class SearchClientServiceImpl implements SearchClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientClient clientClient;

    @Override
    public void insertOrUpdate(String id) {
        Client client = clientClient.findClientById(id);
        SearchClient searchClient = getSearchClientFromClient(client);
        clientRepository.save(searchClient);
    }

    @Override
    public SearchClient findClientById(String clientId) {
        Optional<SearchClient> client = clientRepository.findById(clientId);
        if (!client.isPresent()) {
            throw new ClException(ExceptionEnum.CLIENT_NOT_FOUND);
        }
        return client.get();
    }

    @Override
    public ClientPageResult search(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        page = page <= 0 ? 1 : page;
        rows = rows <= 0 ? 5 : rows;
        page--;
        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(null, null));
        // 分页
        queryBuilder.withPageable(PageRequest.of(page, rows));
        // 查询条件
        if (!StringUtils.isBlank(key)) {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.should(QueryBuilders.matchQuery("id",key));
            boolQuery.should(QueryBuilders.matchQuery("name",key));
            queryBuilder.withQuery(boolQuery);
        }
        // 排序
        if (!StringUtils.isBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        Page<SearchClient> clients = clientRepository.search(queryBuilder.build());
        if (CollectionUtils.isEmpty(clients.getContent())) {
            throw new ClException(ExceptionEnum.SEARCH_CLIENTS_FAIL);
        }
        ClientPageResult clientPageResult = new ClientPageResult();
        clientPageResult.setData(clients.getContent());
        clientPageResult.setTotal(clients.getTotalElements());
        clientPageResult.setTotalPage(clients.getTotalPages());
        return clientPageResult;
    }

    private SearchClient getSearchClientFromClient(Client client) {
        SearchClient searchClient = new SearchClient();
        searchClient.setId(client.getId());
        searchClient.setName(client.getName());
        searchClient.setBirthday(client.getBirthday());
        searchClient.setSex(client.getSex() ? "男" : "女");
        searchClient.setPhone(client.getPhone());
        searchClient.setImage(client.getImage());
        searchClient.setUpdate(new Date());

        SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String birthday = birthdayFormat.format(searchClient.getBirthday());
        String update = sdf.format(searchClient.getUpdate());

        String all = client.getId() + " " + client.getName() + " " + birthday + " " + searchClient.getSex() + " "
                + client.getPhone() + " " + client.getImage() + " " + update;
        searchClient.setAll(all);
        return searchClient;
    }

}
