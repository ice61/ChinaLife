package com.chinalife.search.service.impl;

import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.insurance.po.Insurance;
import com.chinalife.manauth.entity.ClerkInfo;
import com.chinalife.manauth.utils.JwtUtils;
import com.chinalife.search.client.InsuranceClient;
import com.chinalife.search.config.JwtProperties;
import com.chinalife.search.po.SearchInsurance;
import com.chinalife.search.repository.InsuranceRepository;
import com.chinalife.search.service.SearchInsuranceService;
import com.chinalife.search.vo.InsurancePageResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(JwtProperties.class)
public class SearchInsuranceServiceImpl implements SearchInsuranceService {

    @Autowired
    private InsuranceRepository insuranceRepository;

    @Autowired
    private InsuranceClient insuranceClient;

    @Autowired
    private JwtProperties prop;

    @Override
    public void insertOrUpdateInsurance(Long orderId) {
        Insurance insurance = insuranceClient.findInsByOrderId(orderId);
        SearchInsurance searchInsurance = getSearchInsurance(insurance);
        insuranceRepository.save(searchInsurance);
    }

    @Override
    public InsurancePageResult search(Integer page, Integer rows,
                                      String sortBy, Boolean desc, String key, String from, String to, String token, String clerkId) {
        page = page <= 0 ? 1 : page;
        rows = rows <= 0 ? 5 : rows;
        page--;
        // 创建查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(null, new String[]{"all"}));
        // 分页
        queryBuilder.withPageable(PageRequest.of(page, rows));
        // 查询条件
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (token != null) {
            try {
                ClerkInfo clerkInfo = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
                boolQuery.must(QueryBuilders.matchQuery("clerkId", clerkInfo.getId()));
            } catch (Exception e) {
                throw new ClException(ExceptionEnum.UNAUTHORIZED);
            }
        }
        if (StringUtils.isNotBlank(clerkId)) {
            boolQuery.must(QueryBuilders.matchQuery("clerkId", clerkId));
        }
        if (!StringUtils.isBlank(key)) {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        }
        if (from != null && to != null) {
            boolQuery.must(QueryBuilders.rangeQuery("created").gte(from).lte(to).format("yyyy-MM-dd"));
        }
        queryBuilder.withQuery(boolQuery);
        // 排序
        if (!StringUtils.isBlank(sortBy)) {
            if (desc == true) {
                queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.DESC));
            } else {
                queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(SortOrder.ASC));
            }
        }
        Page<SearchInsurance> result = insuranceRepository.search(queryBuilder.build());

        if (CollectionUtils.isEmpty(result.getContent())) {
            throw new ClException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        InsurancePageResult insurancePageResult = new InsurancePageResult();
        insurancePageResult.setData(result.getContent());
        insurancePageResult.setTotal(result.getTotalElements());
        insurancePageResult.setTotalPage(result.getTotalPages());
        return insurancePageResult;
    }

    @Override
    public void deleteInsurance(Long id) {
        Optional<SearchInsurance> opt = insuranceRepository.findById(id);
        if (!opt.isPresent()) {
            throw new ClException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        SearchInsurance searchInsurance = opt.get();
        insuranceRepository.delete(searchInsurance);
    }


    private SearchInsurance getSearchInsurance(Insurance insurance) {
        SearchInsurance searchInsurance = new SearchInsurance();
        searchInsurance.setOrderId(insurance.getOrderId());
        searchInsurance.setClientId(insurance.getClientId());
        searchInsurance.setClientName(insurance.getClientName());
        searchInsurance.setSex(insurance.getSex() ? "男" : "女");
        searchInsurance.setPhone(insurance.getPhone());
        searchInsurance.setImage(insurance.getImage());
        searchInsurance.setBirthday(insurance.getBirthday());
        searchInsurance.setSort(insurance.getSort());
        searchInsurance.setMoney(insurance.getMoney());
        searchInsurance.setClientfId(insurance.getClientfId());
        searchInsurance.setClientfName(insurance.getClientfName());
        searchInsurance.setClientsId(insurance.getClientsId());
        searchInsurance.setClientsName(insurance.getClientsName());
        searchInsurance.setClerkId(insurance.getClerkId());
        searchInsurance.setCreated(insurance.getCreated());
        searchInsurance.setUpdated(insurance.getUpdated());

        SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String birthday = birthdayFormat.format(searchInsurance.getBirthday());
        String created = sdf.format(searchInsurance.getUpdated());
        String updated = sdf.format(searchInsurance.getUpdated());

        String all = insurance.getOrderId() + " " + insurance.getOrderId() + " " + insurance.getClientId() + " " + insurance.getClientName() + " " +
                searchInsurance.getSex() + " " + insurance.getPhone() + " " + insurance.getImage() + " " + birthday + " "
                + insurance.getSort() + " " + insurance.getMoney() + " " + insurance.getClientfId() + " " + insurance.getClientfName() + " "
                + insurance.getClientsId() + " " + insurance.getClientsName() + " " + insurance.getClerkId() + " " + created
                + " " + updated;
        searchInsurance.setAll(all);
        return searchInsurance;
    }
}
