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
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@Transactional
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    WmNewsMapper wmNewsMapper;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private WmChannelMapper wmChannelMapper;
    @Autowired
    private IArticleClient articleClient;

    /**
     * To comment
     */
    @Override
    public void autoScanWmNews(Integer articleId) {
        WmNews wmNews = wmNewsMapper.selectById(articleId);
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

        dto.setCreateTime(new Date());

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
