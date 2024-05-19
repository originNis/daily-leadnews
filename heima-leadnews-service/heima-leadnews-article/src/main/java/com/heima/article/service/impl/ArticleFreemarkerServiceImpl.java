package com.heima.article.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/19
 */
@Service
@Transactional
@Slf4j
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    ApArticleMapper apArticleMapper;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;
    @Autowired
    Configuration freemarkerConfiguration;
    @Autowired
    FileStorageService fileStorageService;

    /**
     * 生成静态文件并上传minio
     *
     * @param apArticle
     * @param content
     */
    @Override
    @Async
    public void buildArticleToMinio(ApArticle apArticle, String content) {
        ApArticleContent article = apArticleContentMapper.selectOne(
                Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, apArticle.getId()));

        try {
            Template template = freemarkerConfiguration.getTemplate("article.ftl");

            Map<String, Object> map = new HashMap<>();
            map.put("content", JSONArray.parseArray(article.getContent()));
            StringWriter stringWriter = new StringWriter();
            template.process(map, stringWriter);

            InputStream in = new ByteArrayInputStream(stringWriter.toString().getBytes());
            String url = fileStorageService.uploadHtmlFile("", article.getArticleId() + ".html", in);

            apArticle.setStaticUrl(url);
            apArticleMapper.update(apArticle, Wrappers.query(new ApArticle()).eq("id", article.getArticleId()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
