package com.atguigu.spzx.manager.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.atguigu.spzx.manager.service.SysUserService;
import com.atguigu.spzx.manager.service.ValidateCodeService;
import com.atguigu.spzx.model.dto.system.LoginDto;
import com.atguigu.spzx.model.entity.system.SysUser;
import com.atguigu.spzx.model.vo.common.Result;
import com.atguigu.spzx.model.vo.common.ResultCodeEnum;
import com.atguigu.spzx.model.vo.system.LoginVo;
import com.atguigu.spzx.model.vo.system.ValidateCodeVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户接口")
@RestController
@RequestMapping(value = "/admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ValidateCodeService validateCodeService;

    /***
    * @description 获取用户信息
    *
    * @return com.atguigu.spzx.model.vo.common.Result<com.atguigu.spzx.model.entity.system.SysUser>
    * @author 乔晓扑
    * @date 2023/10/27 16:25
    */
    @GetMapping(value = "/getUserInfo")
    public Result<SysUser> getUserInfo(@RequestHeader(name = "token") String token){
//        String token = request.getHeader("token");
        return Result.build(sysUserService.getUserInfo(token),ResultCodeEnum.SUCCESS);
    }
    /***
     * @description 生成图片验证码
     * @param
     * @return
     * @author 乔晓扑
     * @date 2023/10/27 15:16
     */
    @GetMapping(value = "/generateValidateCode")
    public Result<ValidateCodeVo> generateValidateCode() {
        ValidateCodeVo validateCodeVo = validateCodeService.generateValidateCode();
        return Result.build(validateCodeVo, ResultCodeEnum.SUCCESS);
    }

    /***
     * @description 用户登录
     * @param
     * @return
     * @author 乔晓扑
     * @date 2023/10/27 10:04
     */
    @PostMapping("login")
    public Result login(@RequestBody LoginDto loginDto) {
        LoginVo loginVo = sysUserService.login(loginDto);
        return Result.build(loginVo, ResultCodeEnum.SUCCESS);
    }
}
