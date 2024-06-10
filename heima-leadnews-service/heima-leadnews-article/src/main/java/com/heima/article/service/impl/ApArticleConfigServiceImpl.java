package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.pojos.ApArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>
 * APP已发布文章配置表 服务实现类
 * </p>
 *
 * @author Rybin
 * @since 2024-05-27
 */
@Service
@Slf4j
@Transactional
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig>
        implements ApArticleConfigService {

    @Override
    public void updateByMap(Map map) {
        update(Wrappers.<ApArticleConfig>lambdaUpdate().eq(ApArticleConfig::getArticleId, map.get("articleId"))
                .set(ApArticleConfig::getIsDown, map.get("enable").equals(1)));
    }
}
