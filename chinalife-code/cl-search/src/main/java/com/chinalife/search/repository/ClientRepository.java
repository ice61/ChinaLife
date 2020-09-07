package com.chinalife.search.repository;

import com.chinalife.search.po.SearchClient;
import com.chinalife.search.po.SearchInsurance;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ClientRepository extends ElasticsearchRepository<SearchClient,String> {
}
