package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/6/6
 */
public interface ArticleSearchService {

    /**
     * es分页搜索文章
     * @param dto
     * @return
     */
    public ResponseResult search(UserSearchDto dto);
}
