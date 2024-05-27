package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {
    /**
     * 根据条件查询文章列表
     * @param wmNewsPageReqDto
     * @return
     */
    public ResponseResult findNewsList(@RequestBody WmNewsPageReqDto wmNewsPageReqDto);

    /**
     * 发布修改文章或保存为草稿
     *
     * @param wmNewsDto
     * @return
     */
    ResponseResult submitNews(WmNewsDto wmNewsDto);

    /**
     * 设置文章上下架
     * @param wmNewsDto
     * @return
     */
    public ResponseResult setEnable(WmNewsDto wmNewsDto);
}
