package com.dromara.auth.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.dromara.auth.service.IAuthStrategy;
import com.dromara.auth.service.SysLoginService;
import com.weiruan.backend.api.RemoteBackendUserService;
import com.weiruan.backend.api.model.BaseLoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import com.dromara.auth.domain.vo.LoginVo;
import com.dromara.auth.util.AESUtil;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.model.LoginBody;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.exception.CaptchaException;
import org.dromara.common.core.exception.user.CaptchaExpireException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.model.LoginUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 手机号+密码认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("phone" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PhoneAuthStrategy implements IAuthStrategy {

    @Value("${security.captcha.enabled}")
    private Boolean captchaEnabled;

    private final SysLoginService loginService;

    @DubboReference
    private RemoteBackendUserService remoteBackendUserService;

    @DubboReference
    private RemoteUserService remoteUserService;

    @Override
    public void validate(LoginBody loginBody) {
        //转换为 PhoneLoginBody
    }

    @Override
    public LoginVo login(String clientId, LoginBody loginBody, RemoteClientVo client) {
        String tenantId = loginBody.getTenantId();
        String phone = loginBody.getPhone();
        String password = loginBody.getPassword();
        String code = loginBody.getCode();
        String uuid = loginBody.getUuid();

        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, phone, code, uuid);
        }

        BaseLoginUser baseUser = remoteBackendUserService.getUserInfoByPhone(phone);
        //前端密码解密
        String decodePassword = AESUtil.decrypt(password);
        loginService.checkLogin(LoginType.PASSWORD, tenantId, phone, () -> !BCrypt.checkpw(decodePassword, baseUser.getPassword()));
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, clientId);
        // 生成token
        LoginUser loginUser = baseUser2LoginUser(baseUser);
        LoginHelper.login(loginUser, model);

        loginService.recordLogininfor(baseUser.getHospitalId().toString(), phone, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        remoteUserService.recordLoginInfo(baseUser.getUserId(), ServletUtils.getClientIP());

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(clientId);
        loginVo.setUserid(loginUser.getUserId().toString());
        loginVo.setUsername(loginUser.getUsername());
        loginVo.setPhone(phone);
        loginVo.setNewUser(null);
        return loginVo;
    }

    private LoginUser baseUser2LoginUser(BaseLoginUser baseUser) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(baseUser.getHospitalId().toString());
        loginUser.setUserId(baseUser.getUserId());
        loginUser.setUserType(baseUser.getUserType().toString());
        loginUser.setDeptId(null);
        loginUser.setDeptName(null);
        loginUser.setUsername(baseUser.getUsername());
        loginUser.setNickname(baseUser.getUsername());
        loginUser.setPassword(baseUser.getPassword());
        loginUser.setRoles(null);
        loginUser.setRoleId(null);

        return loginUser;

    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    private void validateCaptcha(String tenantId, String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

}
