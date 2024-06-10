package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.search.dtos.SearchArticleDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/6/10
 */
@Component
@Slf4j
public class SyncArticleListener {
    @Autowired
    RestHighLevelClient esClient;

    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message) {
        if (StringUtils.isNotBlank(message)) {
            log.info("Article service accept message:{}", message);

            SearchArticleDto dto = JSON.parseObject(message, SearchArticleDto.class);
            IndexRequest indexRequest = new IndexRequest("app_info_article");
            indexRequest.id(dto.getId().toString());
            indexRequest.source(message, XContentType.JSON);
            try {
                esClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Article sync listener error: {}", e);
            }
        }
    }
}
