package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;
import org.springframework.stereotype.Service;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/19
 */
@Service
public interface ArticleFreemarkerService {

    /**
     * 生成静态文件并上传minio
     * @param apArticle
     * @param content
     */
    public void buildArticleToMinio(ApArticle apArticle, String content);
}
