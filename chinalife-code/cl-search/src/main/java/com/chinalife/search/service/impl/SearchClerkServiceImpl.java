package com.chinalife.search.service.impl;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.common.enums.ExceptionEnum;
import com.chinalife.common.exception.ClException;
import com.chinalife.search.client.ClerkClient;
import com.chinalife.search.client.ScoreClient;
import com.chinalife.search.po.Clerk;
import com.chinalife.search.repository.ClerkRepository;
import com.chinalife.search.service.SearchClerkService;
import com.chinalife.search.vo.ClerkPageResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Service
public class SearchClerkServiceImpl implements SearchClerkService {

    @Autowired
    private ClerkClient clerkClient;

    @Autowired
    private ScoreClient scoreClient;

    @Autowired
    private ClerkRepository clerkRepository;


    @Override
    public void insertOrUpdateClerk(String id) {
        SearchClerk searchClerk = clerkClient.findClerkById(id);
        Clerk clerk = createClerk(searchClerk);
        clerkRepository.save(clerk);
    }

    @Override
    public String findImageById(String id) {
        Optional<Clerk> result = clerkRepository.findById(id);
        if (!result.isPresent()) {
            throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
        }
        String image = result.get().getImage();
        if (StringUtils.isBlank(image)) {
            throw new ClException(ExceptionEnum.NO_IMAGE);
        }
        return image;
    }

    @Override
    public ClerkPageResult search(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
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
        if (!StringUtils.isBlank(key)) {
            queryBuilder.withQuery(QueryBuilders.matchQuery("all", key));
        }
        // 排序
        if (!StringUtils.isBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(desc ? SortOrder.DESC : SortOrder.ASC));
        }
        Page<Clerk> result = clerkRepository.search(queryBuilder.build());
        if (CollectionUtils.isEmpty(result.getContent())) {
            throw new ClException(ExceptionEnum.SEARCH_CLERKS_FAIL);
        }
        ClerkPageResult clerkPageResult = new ClerkPageResult();
        clerkPageResult.setData(result.getContent());
        clerkPageResult.setTotal(result.getTotalElements());
        clerkPageResult.setTotalPage(result.getTotalPages());
        return clerkPageResult;
    }

    @Override
    public void deleteClerk(String id) {
        Optional<Clerk> result = clerkRepository.findById(id);
        if (result.isPresent()) {
            clerkRepository.delete(result.get());
        }
    }

    @Override
    public Clerk findClerkById(String id) {
        Optional<Clerk> clerk = clerkRepository.findById(id);
        if (!clerk.isPresent()) {
            throw new ClException(ExceptionEnum.FIND_CLERK_FAIL);
        }
        return clerk.get();

    }

    private Clerk createClerk(SearchClerk searchClerk) {
        Clerk clerk = new Clerk();
        clerk.setId(searchClerk.getId());
        clerk.setInstiution(searchClerk.getInstiution());
        clerk.setName(searchClerk.getName());
        clerk.setSex(searchClerk.getSex() == true ? "男" : "女");
        clerk.setBirthday(searchClerk.getBirthday());
        clerk.setPhone(searchClerk.getPhone());
        clerk.setImage(searchClerk.getImage());
        clerk.setCreated(searchClerk.getCreated());
        clerk.setUpdated(searchClerk.getUpdated());
        clerk.setScore(scoreClient.findScoreById(searchClerk.getId()));

        SimpleDateFormat birthdayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String birthday = birthdayFormat.format(searchClerk.getBirthday());
        String updated = sdf.format(searchClerk.getUpdated());
        String created = sdf.format(searchClerk.getCreated());
        String all = searchClerk.getId() + " " + searchClerk.getInstiution() + " " + searchClerk.getName() + " " + clerk.getSex() +
                " " + birthday + " " + updated + " " + created + " " + clerk.getScore();
        clerk.setAll(all);
        return clerk;
    }
}
