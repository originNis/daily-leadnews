package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Override
    public ResponseResult login(LoginDto dto) {
        // 账户登录
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            ApUser user = getOne(Wrappers.query(new ApUser()).eq("phone", dto.getPhone()));

            if (user == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST, "用户信息不存在");
            }

            // 加盐计算md5并与数据库内密码比对
            String salt = user.getSalt();
            String password = dto.getPassword();
            String encryted = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!encryted.equals(user.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            Map<String, Object> map = new HashMap<>();
            String token = AppJwtUtil.getToken(user.getId().longValue());
            map.put("token", token);
            user.setSalt("");
            user.setPassword("");
            map.put("user", user);

            return ResponseResult.okResult(map);
        } else {
            Map<String, Object> map = new HashMap<>();
            // 游客登录则取id=0的token
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }
    }
}
