package org.dromara.common.mybatis.config;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.dromara.common.mybatis.handler.CreateAndUpdateMetaObjectHandler;
import org.dromara.common.mybatis.interceptor.PlusDataPermissionInterceptor;
import org.dromara.common.satoken.utils.LoginHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

/**
 * mybatis-plus配置类(下方注释有插件介绍)
 *
 * @author Lion Li
 */
@AutoConfiguration
@EnableTransactionManagement(proxyTargetClass = true)
@MapperScan("${mybatis-plus.mapperPackage}")
@PropertySource(value = "classpath:common-mybatis.yml", factory = YmlPropertySourceFactory.class)
@Slf4j
public class MybatisPlusConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限处理
        interceptor.addInnerInterceptor(dataPermissionInterceptor());
        // 分页插件
//        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
//        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        //租户插件
        interceptor.addInnerInterceptor(tenantLineInnerInterceptor());
        return interceptor;
    }

    /**
     * 租户插件
     * @return
     */
    public TenantLineInnerInterceptor tenantLineInnerInterceptor() {
        //多租户插件
        return new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                log.debug("MyBatisPlus -> getTenantId(): {}", LoginHelper.getHospitalId());
                return new LongValue(LoginHelper.getHospitalId());
            }

            // 这是 default 方法,默认返回 false 表示所有表都需要拼多租户条件
            @Override
            public boolean ignoreTable(String tableName) {
                String ignoreTableStr = "sys_user,sys_client,t_roles,sys_menu,sys_user_menu_permission,sys_operation,sys_user_operation_permission,dual,user_package_detail,sys_dict,dictionary,dictionary_folder";
                List<String> ignoreTables = List.of(ignoreTableStr.split(","));
                log.debug("MyBatisPlus忽略的表名: {}", JSON.toJSONString(ignoreTables));
                boolean contains = ignoreTables.contains(tableName);
                log.debug("MyBatisPlus忽略表名: {}, 结果: {}", tableName, contains);
                return contains;
            }

            @Override
            public String getTenantIdColumn() {
                return "hospital_id";
            }

        });

    }

    /**
     * 数据权限拦截器
     */
    public PlusDataPermissionInterceptor dataPermissionInterceptor() {
        return new PlusDataPermissionInterceptor();
    }

    /**
     * 分页插件，自动识别数据库类型
     */
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInnerInterceptor.setMaxLimit(-1L);
        // 分页合理化
        paginationInnerInterceptor.setOverflow(true);
        return paginationInnerInterceptor;
    }

    /**
     * 乐观锁插件
     */
    public OptimisticLockerInnerInterceptor optimisticLockerInnerInterceptor() {
        return new OptimisticLockerInnerInterceptor();
    }

    /**
     * 元对象字段填充控制器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new CreateAndUpdateMetaObjectHandler();
    }

    /**
     * 使用网卡信息绑定雪花生成器
     * 防止集群雪花ID重复
     */
    @Primary
    @Bean
    public IdentifierGenerator idGenerator() {
        return new DefaultIdentifierGenerator(NetUtil.getLocalhost());
    }

    /**
     * PaginationInnerInterceptor 分页插件，自动识别数据库类型
     * https://baomidou.com/pages/97710a/
     * OptimisticLockerInnerInterceptor 乐观锁插件
     * https://baomidou.com/pages/0d93c0/
     * MetaObjectHandler 元对象字段填充控制器
     * https://baomidou.com/pages/4c6bcf/
     * ISqlInjector sql注入器
     * https://baomidou.com/pages/42ea4a/
     * BlockAttackInnerInterceptor 如果是对全表的删除或更新操作，就会终止该操作
     * https://baomidou.com/pages/f9a237/
     * IllegalSQLInnerInterceptor sql性能规范插件(垃圾SQL拦截)
     * IdentifierGenerator 自定义主键策略
     * https://baomidou.com/pages/568eb2/
     * TenantLineInnerInterceptor 多租户插件
     * https://baomidou.com/pages/aef2f2/
     * DynamicTableNameInnerInterceptor 动态表名插件
     * https://baomidou.com/pages/2a45ff/
     */

}
