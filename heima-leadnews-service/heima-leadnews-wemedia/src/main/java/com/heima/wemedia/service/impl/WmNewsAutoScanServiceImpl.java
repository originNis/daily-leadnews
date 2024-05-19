package com.heima.wemedia.service.impl;

import com.heima.apis.article.IArticleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private IArticleClient articleClient;

    /**
     * 审核内容并上架
     */
    @Override
    @Async
    public void autoScanWmNews(Integer newsId) {
        WmNews wmNews = wmNewsMapper.selectById(newsId);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanImpl - article don`t exist.");
        }

        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            ResponseResult response = saveAppArticle(wmNews);
            if (!response.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanImpl - save article failed.");
            }
            wmNews.setArticleId((Long) response.getData());
            updateWmNews(wmNews, (short) 9, "publish successfully");
        }
    }

    /**
     * To comment
     * @param wmNews
     * @return
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, dto);

        dto.setAuthorId(wmNews.getUserId().longValue());

        WmUser author = wmUserMapper.selectById(wmNews.getUserId());
        if (author.getName() != null) {
            dto.setAuthorName(author.getName());
        }

        WmChannel channel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (channel.getName() != null) {
            dto.setChannelName(channel.getName());
        }

        dto.setCreatedTime(new Date());

        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }

        ResponseResult result = articleClient.saveArticle(dto);

        return result;
    }

    private void updateWmNews(WmNews wmNews, Short status, String msg) {
        wmNews.setStatus(status);
        wmNews.setReason(msg);
        wmNewsMapper.updateById(wmNews);
    }
}
