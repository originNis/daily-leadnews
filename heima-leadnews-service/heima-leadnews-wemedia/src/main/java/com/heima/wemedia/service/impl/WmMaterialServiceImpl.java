package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    FileStorageService fileStorageService;
    /**
     * 上传图片等文件
     *
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadFile(MultipartFile multipartFile) {
        // 1.检查参数
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        // 向minIO上传文件
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        String originalName = multipartFile.getOriginalFilename();
        String[] splits = originalName.split("\\.");
        String postFix = splits[1];

        try {
            // 2.1根据后缀判断：上传图片
            if (postFix.equals("png") || postFix.equals("jpg") || postFix.equals("jpeg")) {
                fileName = fileStorageService.uploadImgFile("", fileName + "." + postFix, multipartFile.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3.文件URL存入数据库
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.get().getId());
        wmMaterial.setUrl(fileName);
        wmMaterial.setType((short) 0);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);
    }

    /**
     * 查询收藏/为收藏的素材列表
     * @param wmMaterialDto
     * @return
     */
    @Override
    public ResponseResult findMaterialList(WmMaterialDto wmMaterialDto) {
        // 1.检查参数
        wmMaterialDto.checkParam();

        // 2.1.分页查询
        IPage page = new Page(wmMaterialDto.getPage(), wmMaterialDto.getSize());

        // 2.2.按照用户查询
        LambdaQueryWrapper<WmMaterial> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(WmMaterial::getUserId, WmThreadLocalUtil.get().getId());

        // 2.3.是否收藏
        if (wmMaterialDto.getIsCollection() != null && wmMaterialDto.getIsCollection() == 1) {
            queryWrapper.eq(WmMaterial::getIsCollection, 1);
        }

        // 2.4.按照时间倒序查询
        queryWrapper.orderByDesc(WmMaterial::getCreatedTime);

        page = page(page, queryWrapper);

        ResponseResult responseResult = new PageResponseResult(
                wmMaterialDto.getPage(), wmMaterialDto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }
}
