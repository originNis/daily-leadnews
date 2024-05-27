package com.heima.article.listner;

import com.alibaba.fastjson.JSON;
import com.heima.article.service.ApArticleConfigService;
import com.heima.common.constants.WmNewsMessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/5/27
 */
@Component
public class ArticleMessageListener {
    @Autowired
    ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void downOrUp(String message) {
        if (! StringUtils.isEmpty(message)) {
            Map map = JSON.parseObject(message, Map.class);

            apArticleConfigService.updateByMap(map);
        }
    }
}
