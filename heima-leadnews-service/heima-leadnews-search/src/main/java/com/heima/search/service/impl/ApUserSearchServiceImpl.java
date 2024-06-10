package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.search.pojos.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/6/10
 */
@Service
@Slf4j
public class ApUserSearchServiceImpl implements ApUserSearchService {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 在数据库保存用户的搜索记录
     *
     * @param keyword
     * @param userId
     */
    @Override
    @Async
    public void insert(String keyword, Integer userId) {
        Query query = new Query(Criteria.where("keyword").is(keyword).and("userId").is(userId));
        ApUserSearch currentResult = mongoTemplate.findOne(query, ApUserSearch.class);

        if (currentResult != null) { // 若搜索词已存在，更新搜索时间
            currentResult.setCreatedTime(new Date());
            mongoTemplate.save(currentResult);
        } else {
            ApUserSearch newSearch = new ApUserSearch();
            newSearch.setKeyword(keyword);
            newSearch.setUserId(userId);
            newSearch.setCreatedTime(new Date());

            Query queryForAllSearch = new Query(Criteria.where("userId").is(userId));
            queryForAllSearch.with(Sort.by(Sort.Direction.DESC, "createdTime"));
            List<ApUserSearch> searchList = mongoTemplate.find(queryForAllSearch, ApUserSearch.class);

            if (searchList == null || searchList.size() < 10) { // 若搜索词不满10个则直接添加
                mongoTemplate.save(newSearch);
            } else { // 否则替换最久的那一条搜索记录
                ApUserSearch lastSearch = searchList.get(searchList.size() - 1);
                mongoTemplate.findAndReplace(new Query(Criteria.where("id").is(lastSearch.getId())), newSearch);
            }
        }
    }

    /**
     * 获取用户的历史搜索记录
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult findUserSearch(Integer userId) {
        if (userId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        List<ApUserSearch> search = mongoTemplate.find(new Query(Criteria.where("userId").is(userId)), ApUserSearch.class);
        return ResponseResult.okResult(search);
    }
}
