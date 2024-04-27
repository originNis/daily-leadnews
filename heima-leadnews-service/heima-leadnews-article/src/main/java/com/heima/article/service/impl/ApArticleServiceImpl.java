package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    ApArticleMapper apArticleMapper;
    @Autowired
    ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    ApArticleContentMapper apArticleContentMapper;

    /**
     * 加载文章列表
     *
     * @param dto
     * @param type
     * @return
     */
    @Override
    public ResponseResult load(ArticleHomeDto dto, Short type) {
        // 校验条数
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }
        dto.setSize(Math.min(size, ArticleConstants.MAX_PAGE_SIZE));

        // 校验查询类型
        if (type == null || (type != ArticleConstants.LOADTYPE_LOAD_MORE && type != ArticleConstants.LOADTYPE_LOAD_NEW)) {
            type = 1;
        }

        // 校验频道
        if (StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        // 校验时间条件
        if (dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());

        // sql查询
        List<ApArticle> list = apArticleMapper.loadArticleList(dto, type);

        return ResponseResult.okResult(list);
    }

    /**
     * 保存/修改文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ArticleDto dto) {
        // 1.检查参数
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle article = new ApArticle();
        BeanUtils.copyProperties(dto, article);

        // 2.1 文章id不存在则保存文章
        if (dto.getId() == null) {
            // 保存文章
            save(article);

            // 保存文章配置
            ApArticleConfig config = new ApArticleConfig(article.getId());
            apArticleConfigMapper.insert(config);

            // 保存文章内容
            ApArticleContent content = new ApArticleContent();
            content.setArticleId(article.getId());
            content.setContent(dto.getContent());
            apArticleContentMapper.insert(content);

        } else { // 2.2 文章id存在则修改文章
            // 修改文章
            updateById(article);

            // 保存文章内容
            ApArticleContent content = apArticleContentMapper.selectOne(
                    Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, article.getId()));
            content.setContent(dto.getContent());
            apArticleContentMapper.updateById(content);
        }

        return ResponseResult.okResult(article.getId());
    }
}
