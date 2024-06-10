package com.heima.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.pojos.ApArticleConfig;

import java.util.Map;

/**
 * <p>
 * APP已发布文章配置表 服务类
 * </p>
 *
 * @author Rybin
 * @since 2024-05-27
 */
public interface ApArticleConfigService extends IService<ApArticleConfig> {
    public void updateByMap(Map map);
}
