package com.chinalife.search.service.impl;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.manauth.utils.JwtUtils;
import com.chinalife.search.config.JwtProperties;
import com.chinalife.search.po.SearchInsurance;
import com.chinalife.search.service.InsuranceGraphService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class InsuranceGraphServiceImpl implements InsuranceGraphService {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private JwtProperties prop;

    @Override
    public Map<String, Long> sortGraph(String from, String to, String token, String clerkId) {
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //创建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
            boolQuery.must(QueryBuilders.rangeQuery("created").gte(from).lte(to).format("yyyy-MM-dd"));
        } else if (StringUtils.isNotBlank(from)) {
            boolQuery.must(QueryBuilders.rangeQuery("created").gte(from).format("yyyy-MM-dd"));
        } else if (StringUtils.isNotBlank(to)) {
            boolQuery.must(QueryBuilders.rangeQuery("created").lte(to).format("yyyy-MM-dd"));
        }
        if (token != null) {
            try {
                String personId = JwtUtils.getInfoFromToken(token, prop.getPublicKey()).getId();
                boolQuery.must(QueryBuilders.matchQuery("clerkId", personId));
            } catch (Exception e) {
                throw new ClException(ExceptionEnum.UNAUTHORIZED);
            }
        }
        if (StringUtils.isNotBlank(clerkId)) {
            boolQuery.must(QueryBuilders.matchQuery("clerkId", clerkId));
        }
        queryBuilder.withQuery(boolQuery);
        // 添加聚合
        String sortAggName = "sort_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(sortAggName).field("sort"));
        // 查询
        AggregatedPage<SearchInsurance> result = template.queryForPage(queryBuilder.build(), SearchInsurance.class);
        // 解析聚合
        Aggregations aggs = result.getAggregations();
        StringTerms terms = aggs.get(sortAggName);
        HashMap<String, Long> sort = new HashMap<>();
        for (StringTerms.Bucket bucket : terms.getBuckets()) {
            sort.put(bucket.getKeyAsString(),bucket.getDocCount());
        }
        return sort;
    }
}
