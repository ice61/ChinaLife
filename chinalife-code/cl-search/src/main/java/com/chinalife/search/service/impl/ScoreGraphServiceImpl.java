package com.chinalife.search.service.impl;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.manauth.utils.JwtUtils;
import com.chinalife.search.config.JwtProperties;
import com.chinalife.search.po.SearchInsurance;
import com.chinalife.search.service.ScoreGraphService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@EnableConfigurationProperties(JwtProperties.class)
public class ScoreGraphServiceImpl implements ScoreGraphService {

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    private JwtProperties prop;

    @Override
    public Map<String, Long> scoreGraph(String from, String to, int interval, String token, String clerkId) {
        //创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //创建bool查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

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
        /*新增添的测试代码*/
        if (StringUtils.isNotBlank(from) && StringUtils.isNotBlank(to)) {
            boolQuery.must(QueryBuilders.rangeQuery("created").gte(from).lte(to).format("yyyy-MM-dd"));
        } else if (StringUtils.isNotBlank(from)) {
            boolQuery.must(QueryBuilders.rangeQuery("created").gte(from).format("yyyy-MM-dd"));
        } else if (StringUtils.isNotBlank(to)) {
            boolQuery.must(QueryBuilders.rangeQuery("created").lte(to).format("yyyy-MM-dd"));
        }

        queryBuilder.withQuery(boolQuery);
        String scoreAggName = "score_Agg";

       /* DateHistogramAggregationBuilder scoreAgg = AggregationBuilders.
                dateHistogram(scoreAggName).field("created").
                order(Histogram.Order.KEY_ASC).minDocCount(0).extendedBounds(new ExtendedBounds(from,to));*/

        DateHistogramAggregationBuilder scoreAgg = AggregationBuilders.
                dateHistogram(scoreAggName).field("created").
                order(Histogram.Order.KEY_ASC).minDocCount(0);

        if (interval == 1) {
            scoreAgg.dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd");
        } else if (interval == 7) {
            scoreAgg.dateHistogramInterval(DateHistogramInterval.WEEK).format("yyyy-MM-dd");
        } else if (interval == 31) {
            scoreAgg.dateHistogramInterval(DateHistogramInterval.MONTH).format("yyyy-MM");
        } else {
            scoreAgg.dateHistogramInterval(DateHistogramInterval.YEAR).format("yyyy");
        }
        queryBuilder.addAggregation(scoreAgg);
        AggregatedPage<SearchInsurance> result = template.queryForPage(queryBuilder.build(), SearchInsurance.class);
        Aggregations aggs = result.getAggregations();
        Histogram agg = aggs.get(scoreAggName);
        HashMap<String, Long> score = new LinkedHashMap<>();
        for (Histogram.Bucket bucket : agg.getBuckets()) {
            score.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return score;
    }

    @Override
    public List<Map<String, Long>> scoreRand(String from, String to, int num, Boolean desc) {
        num = num <= 0 ? 3 : num;
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
        queryBuilder.withQuery(boolQuery);
        String rankAggName = "rankAgg";
        if (desc == false) {
            queryBuilder.addAggregation(AggregationBuilders.terms(rankAggName).field("clerkId").order(Terms.Order.count(true)).size(num));
        } else {
            queryBuilder.addAggregation(AggregationBuilders.terms(rankAggName).field("clerkId").order(Terms.Order.count(false)).size(num));
        }
        AggregatedPage<SearchInsurance> result = template.queryForPage(queryBuilder.build(), SearchInsurance.class);
        Aggregations aggs = result.getAggregations();
        StringTerms terms = aggs.get(rankAggName);
        LinkedList<Map<String, Long>> rank = new LinkedList<Map<String, Long>>();
        for (StringTerms.Bucket bucket : terms.getBuckets()) {
            HashMap<String, Long> temp = new HashMap<>();
            temp.put(bucket.getKeyAsString(),bucket.getDocCount());
            rank.addLast(temp);
        }
        return rank;
    }


}
