package com.atguigu.spzx.manager.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.atguigu.spzx.common.exception.GuiguException;
import com.atguigu.spzx.manager.mapper.SysUserMapper;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Override
    public LoginVo login(LoginDto loginDto) {

        //验证码校验
        String captcha = loginDto.getCaptcha();
        String key = loginDto.getCodeKey();
        String redisCode = redisTemplate.opsForValue().get("user:validate" + key);

        if(StrUtil.isEmpty(redisCode)||!StrUtil.equalsIgnoreCase(redisCode,captcha)){
            throw new GuiguException(ResultCodeEnum.VALIDATECODE_ERROR);
        }
        redisTemplate.delete("user:validate" + key);
        //1.获取用户名
        String userName = loginDto.getUserName();
        //2.根据用户名查询数据库
        SysUser sysUser = sysUserMapper.selectByUserName(userName);
        if (sysUser == null) {
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }
        String databasePassword = sysUser.getPassword();
        String inputPassword = loginDto.getPassword();

        inputPassword = DigestUtils.md5DigestAsHex(inputPassword.getBytes());

        if(!inputPassword.equals(databasePassword)){
            throw new GuiguException(ResultCodeEnum.LOGIN_ERROR);
        }

        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set("user:login"+token, JSON.toJSONString(sysUser),7, TimeUnit.DAYS);
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);

        return loginVo;
    }

    @Override
    public SysUser getUserInfo(String token) {
        String userJson = redisTemplate.opsForValue().get("user:login" + token);
        return JSON.parseObject(userJson, SysUser.class);
    }
}
