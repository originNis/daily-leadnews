package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/6/10
 */
public interface ApUserSearchService {
    /**
     * 在数据库保存用户的搜索记录
     * @param keyword
     * @param userId
     */
    public void insert(String keyword, Integer userId);

    /**
     * 获取用户的历史搜索记录
     *
     * @param userId
     * @return
     */
    public ResponseResult findUserSearch(Integer userId);
}
