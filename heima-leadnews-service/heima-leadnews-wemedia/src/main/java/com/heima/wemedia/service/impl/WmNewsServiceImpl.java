package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    /**
     * 根据条件查询文章列表
     * @param wmNewsPageReqDto
     * @return
     */
    @Override
    public ResponseResult findNewsList(WmNewsPageReqDto wmNewsPageReqDto) {
        // 1.参数检查
        wmNewsPageReqDto.checkParam();

        // 2.1.分页查询
        IPage page = new Page(wmNewsPageReqDto.getPage(), wmNewsPageReqDto.getSize());


        LambdaQueryWrapper<WmNews> query = new LambdaQueryWrapper<>();

        // 2.2.根据Status查询
        query.eq(wmNewsPageReqDto.getStatus() != null,
                WmNews::getStatus,
                wmNewsPageReqDto.getStatus());

        // 2.3.根据频道查询
        query.eq(wmNewsPageReqDto.getChannelId() != null,
                WmNews::getChannelId,
                wmNewsPageReqDto.getChannelId());

        // 2.4.根据关键字查询
        query.like(StringUtils.isNotBlank(wmNewsPageReqDto.getKeyword()),
                WmNews::getTitle,
                wmNewsPageReqDto.getKeyword());

        // 2.5.根据日期查询
        if (wmNewsPageReqDto.getBeginPubDate() != null && wmNewsPageReqDto.getEndPubDate() != null) {
            query.between(WmNews::getPublishTime,
                    wmNewsPageReqDto.getBeginPubDate(),
                    wmNewsPageReqDto.getEndPubDate());
        }

        // 2.6.根据用户id查询
        query.eq(WmNews::getUserId, WmThreadLocalUtil.get().getId());

        // 2.7.根据日期排序
        query.orderByDesc(WmNews::getPublishTime);

        // 3.查询
        page = page(page, query);

        // 4.设置返回信息并返回
        ResponseResult response = new PageResponseResult(wmNewsPageReqDto.getPage(),
                wmNewsPageReqDto.getSize(),
                (int) page.getTotal());
        response.setData(page.getRecords());

        return response;
    }
}
