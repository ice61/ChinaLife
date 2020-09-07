package com.chinalife.search.repository;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.search.po.Clerk;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ClerkRepository extends ElasticsearchRepository<Clerk, String> {

}
