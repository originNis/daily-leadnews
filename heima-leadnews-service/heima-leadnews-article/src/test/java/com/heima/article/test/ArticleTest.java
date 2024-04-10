package com.heima.article.test;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleTest {

    @Autowired
    ApArticleMapper apArticleMapper;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;
    @Autowired
    Configuration freemarkerConfiguration;
    @Autowired
    FileStorageService fileStorageService;

    @Test
    public void createStaticUrl() {
        try {
            ApArticleContent article = apArticleContentMapper.selectOne(Wrappers.query(new ApArticleContent()).eq("article_id", 1302862387124125698L));

            if (article != null && StringUtils.isNotBlank(article.getContent())) {
                Template template = freemarkerConfiguration.getTemplate("article.ftl");

                Map<String, Object> map = new HashMap<>();
                map.put("content", JSONArray.parseArray(article.getContent()));
                StringWriter stringWriter = new StringWriter();
                template.process(map, stringWriter);

                InputStream in = new ByteArrayInputStream(stringWriter.toString().getBytes());
                String url = fileStorageService.uploadHtmlFile("", article.getArticleId() + ".html", in);

                ApArticle apArticle = new ApArticle();
                apArticle.setStaticUrl(url);
                apArticleMapper.update(apArticle, Wrappers.query(new ApArticle()).eq("id", article.getArticleId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
