package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;

@Component
public class IArticleClientFallback implements IArticleClient {

    @Override
    public ResponseResult saveArticle(ArticleDto dto){
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR, "server down");
    }
}
