package org.dromara.common.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.model.LoginUser;

import java.util.Date;

/**
 * MP注入处理器
 *
 * @author Lion Li
 */
@Slf4j
public class CreateAndUpdateMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            /*if (ObjectUtil.isNotNull(metaObject)
                && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = ObjectUtil.isNotNull(baseEntity.getCreateTime())
                    ? baseEntity.getCreateTime() : new Date();
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);
                LoginUser loginUser = getLoginUser();
                Long userId = ObjectUtil.isNotNull(baseEntity.getCreateBy())
                    ? baseEntity.getCreateBy() : loginUser.getUserId();
                // 当前已登录 且 创建人为空 则填充
                baseEntity.setCreateBy(userId);
                // 当前已登录 且 更新人为空 则填充
                baseEntity.setUpdateBy(userId);
                baseEntity.setCreateDept(ObjectUtil.isNotNull(baseEntity.getCreateDept())
                    ? baseEntity.getCreateDept() : loginUser.getDeptId());
            }*/

            //我自己的 BaseEntityWithCrtAndHospitalId
            if (ObjectUtil.isNotNull(metaObject)
                /*&& metaObject.getOriginalObject() instanceof MxlBaseEntity mxlBaseEntity*/) {
                Integer hospitalId = LoginHelper.getHospitalId();
                Long userId = LoginHelper.getUserId();
                if (userId == null) {
                    this.strictInsertFill(metaObject, "hospitalId", Integer.class, hospitalId);
                    this.strictInsertFill(metaObject, "crtTime", Date.class, new Date());
                    this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
                    return;
                }
                String userName = LoginHelper.getUsername();
                log.debug("MyBatis Plus 开始填充 {} 属性hospital_id: {}, userId: {}, userName: {}", metaObject.getOriginalObject(), hospitalId, userId, userId);
                // 起始版本 3.3.0(推荐使用)
                this.strictInsertFill(metaObject, "hospitalId", Integer.class, hospitalId);
                // 起始版本 3.3.3(推荐)
                this.strictInsertFill(metaObject, "createUserId", Long.class, userId);
                this.strictInsertFill(metaObject, "createUserName", String.class, userName);
                this.strictInsertFill(metaObject, "createUsername", String.class, userName);
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());

                this.strictInsertFill(metaObject, "crtUserId", Long.class, userId);
                this.strictInsertFill(metaObject, "crtUserName", String.class, userName);
                this.strictInsertFill(metaObject, "crtUsername", String.class, userName);
                this.strictInsertFill(metaObject, "crtTime", Date.class, new Date());

//                mxlBaseEntity.setHospitalId(hospitalId);
//                mxlBaseEntity.setCreateUserId(userId);
//                mxlBaseEntity.setCreateUserName(userName);
//                mxlBaseEntity.setCreateTime(new Date());
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 -> insertFill => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject)
                && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = new Date();
                // 更新时间填充(不管为不为空)
                baseEntity.setUpdateTime(current);
                LoginUser loginUser = getLoginUser();
                // 当前已登录 更新人填充(不管为不为空)
                if (ObjectUtil.isNotNull(loginUser)) {
                    baseEntity.setUpdateBy(loginUser.getUserId());
                }
            }

            //我自己的
            if (ObjectUtil.isNotNull(metaObject)) {
                Integer hospitalId = LoginHelper.getHospitalId();
                Long userId = LoginHelper.getUserId();
                if (userId == null) {
                    return;
                }
                String userName = LoginHelper.getUsername();
                log.debug("MyBatis Plus 开始填充 {} 属性hospital_id: {}, userId: {}, userName: {}", metaObject.getOriginalObject(), hospitalId, userId, userId);
                this.strictUpdateFill(metaObject, "updateUserid", Long.class, userId);
                this.strictUpdateFill(metaObject, "updateUserId", Long.class, userId);
                this.strictUpdateFill(metaObject, "updateUsername", String.class, userName);
                this.strictUpdateFill(metaObject, "updateUserName", String.class, userName);
                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 -> updateFill => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    /**
     * 获取登录用户
     */
    private LoginUser getLoginUser() {
        LoginUser loginUser;
        try {
            loginUser = LoginHelper.getLoginUser();
        } catch (Exception e) {
            log.warn("自动注入警告 => 用户未登录");
            return null;
        }
        return loginUser;
    }

}
