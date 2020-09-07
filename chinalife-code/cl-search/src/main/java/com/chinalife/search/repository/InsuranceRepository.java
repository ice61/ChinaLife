package com.chinalife.search.repository;

import com.chinalife.search.po.SearchInsurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface InsuranceRepository extends ElasticsearchRepository<SearchInsurance,Long> {


}
