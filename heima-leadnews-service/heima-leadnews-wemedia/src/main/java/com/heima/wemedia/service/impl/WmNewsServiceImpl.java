package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {
    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;
    @Autowired
    WmMaterialMapper wmMaterialMapper;
    @Autowired
    WmNewsAutoScanService wmNewsAutoScanService;

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

    /**
     * 发布修改文章或保存为草稿
     *
     * @param wmNewsDto
     * @return
     */
    @Override
    public ResponseResult submitNews(WmNewsDto wmNewsDto) {
        if (wmNewsDto == null || wmNewsDto.getContent() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        WmNews news = new WmNews();
        // 将类型与名称相同属性拷贝
        BeanUtils.copyProperties(wmNewsDto, news);
        if (wmNewsDto.getImages() != null && wmNewsDto.getImages().size() > 0) {
            news.setImages(StringUtils.join(wmNewsDto.getImages(), ","));
        }
        if (wmNewsDto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            news.setType(null);
        }
        // 上传数据库
        updateWmNews(news);

        // 如果是草稿则直接结束
        if (wmNewsDto.getStatus().equals(WmNews.Status.NORMAL.getCode())) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        // 不是草稿，则需要保存内容图片与素材的关系
        List<String> materialUrls = extractUrlInfo(wmNewsDto.getContent());
        saveRelationsBewteenNewsAndMaterials(materialUrls, wmNewsDto.getId(), wmNewsDto.getType());
        // 保存封面图片
        saveRelationInfoForCover(wmNewsDto, news, materialUrls);

        wmNewsAutoScanService.autoScanWmNews(news.getId());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    // 注释
    private void saveRelationInfoForCover(WmNewsDto dto, WmNews news, List<String> materials) {
        List<String> images = dto.getImages();

        // 如果封面类型为自动，则重新设置封面类型
        if (dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)) {
            if (materials.size() >= 3) {
                news.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            } else if (materials.size() >= 1 && materials.size() < 3) {
                news.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            } else {
                news.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

//            if (images != null || images.size() > 0) {
//               news.setImages(images);
//            }

            updateById(news);
        }

        if (images != null || images.size() > 0) {
            saveRelationsBewteenNewsAndMaterials(images, news.getId(), WemediaConstants.WM_COVER_REFERENCE);
        }
    }
    
    private void saveRelationsBewteenNewsAndMaterials(List<String> materialUrls, Integer newsId, Short type) {
        if (materialUrls != null && materialUrls.size() != 0) {
            List<WmMaterial> materials = wmMaterialMapper
                    .selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materialUrls));
            // 如果查出的素材不对，则向上抛出异常，便于上层函数执行回滚
            if (materials == null || materials.size() != materialUrls.size()) {
                throw new CustomException(AppHttpCodeEnum.MATERIAL_REFRENECE_FAIL);
            }

            List<Integer> mIds = materials.stream().map(WmMaterial::getId).collect(Collectors.toList());
            wmNewsMaterialMapper.saveRelations(mIds, newsId, type);
        }
    }

    /**
     * 将文章内容中所有图片的URL保存在List中并返回
     * @param content
     * @return
     */
    private List<String> extractUrlInfo(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if (map.get("type").equals("image")) {
                materials.add((String) map.get("value"));
            }
        }
        return materials;
    }

    /**
     * 用于保存或修改文章到数据库
     * @param news
     */
    private void updateWmNews(WmNews news) {
        news.setUserId(WmThreadLocalUtil.get().getId());
        news.setCreatedTime(new Date());
        news.setSubmitedTime(new Date());
        news.setPublishTime(new Date());
        news.setEnable((short) 1);

        // 新建文章，保存
        if (news.getId() == null) {
            save(news);
        } else {
            // 修改文章
            // 首先删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, news.getId()));
            updateById(news);
        }
    }
}
