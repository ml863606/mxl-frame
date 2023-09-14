package com.dromara.auth.controller;

import cn.hutool.core.util.StrUtil;
import com.dromara.auth.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import com.dromara.auth.domain.vo.LoginVo;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.domain.model.LoginBody;
import org.dromara.system.api.RemoteClientService;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * token 控制
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class LoginController {

    @DubboReference
    private final RemoteClientService remoteClientService;

    /**
     * 登录方法
     */
    @PostMapping("/loginByPhone")
    public R loginByPhone(@RequestBody LoginBody loginBody) {
        // 授权类型和客户端id
        String clientId = loginBody.getClientId();
        String grantType = loginBody.getGrantType();
        if (StrUtil.isEmpty(clientId)) {
            clientId = loginBody.getPlatform();
            loginBody.setClientId(clientId);
        }
        RemoteClientVo clientVo = remoteClientService.queryByClientId(clientId);

        // 查询不到 client 或 client 内不包含 grantType
//        if (ObjectUtil.isNull(clientVo) || !StringUtils.contains(clientVo.getGrantType(), grantType)) {
//            log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType);
//            return R.fail(MessageUtils.message("auth.grant.type.error"));
//        }
        if (StrUtil.isEmpty(grantType)) {
            //根据grantType去走不同的策略
            loginBody.setGrantType("phone");
        }
        clientVo.setDeviceType(loginBody.getPlatform());
        LoginVo loginVo = IAuthStrategy.login(loginBody, clientVo);
        return R.ok(loginVo);
    }

}
