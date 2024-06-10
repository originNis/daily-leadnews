package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.common.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Rybin
 * @description:
 * @date: 2024/6/10
 */
@RestController
@RequestMapping("/api/v1/history")
public class ApUserSearchController {
    @Autowired
    ApUserSearchService apUserSearchService;

    @PostMapping("/load")
    public ResponseResult findUserSearch() {
        ApUser apUser = AppThreadLocalUtil.get();
        return apUserSearchService.findUserSearch(apUser == null ? null : apUser.getId());
    }
}
