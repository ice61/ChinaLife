package com.chinalife.search.service;

import com.chinalife.clerk.vo.SearchClerk;
import com.chinalife.search.po.Clerk;
import com.chinalife.search.vo.ClerkPageResult;

public interface SearchClerkService {
    void insertOrUpdateClerk(String id);

    String findImageById(String id);

    ClerkPageResult search(Integer page,Integer rows,String sortBy,Boolean desc,String key);

    void deleteClerk(String id);

    Clerk findClerkById(String id);
}
