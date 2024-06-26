package com.heima.wemedia.controller.v1;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {
    @Autowired
    WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findNewsList(@RequestBody WmNewsPageReqDto wmNewsPageReqDto) {
        return wmNewsService.findNewsList(wmNewsPageReqDto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto wmNewsDto) {
        return wmNewsService.submitNews(wmNewsDto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult setEnable(@RequestBody WmNewsDto wmNewsDto) {
        return wmNewsService.setEnable(wmNewsDto);
    }
}
