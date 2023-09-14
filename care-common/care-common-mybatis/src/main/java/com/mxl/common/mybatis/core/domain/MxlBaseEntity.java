package org.dromara.common.mybatis.core.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 抽象实体
 *
 * @author lengleng
 * @date 2021/8/9
 */
@Getter
@Setter
public class MxlBaseEntity {

	/**
	 * 创建者id
	 */
	@TableField(value = "create_userid", fill = FieldFill.INSERT)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long createUserId;

	/**
	 * 创建者姓名
	 */
	@TableField(value = "create_username", fill = FieldFill.INSERT)
	private String createUserName;

	/**
	 * 创建时间
	 */
	@TableField(value = "create_time", fill = FieldFill.INSERT)
	private Date createTime;

	/**
	 * 更新者
	 */
	@TableField(value = "update_userid", fill = FieldFill.UPDATE)
	private Long updateUserid;

	/**
	 * 更新者
	 */
	@TableField(value = "update_username", fill = FieldFill.UPDATE)
	private String updateUsername;

	/**
	 * 更新时间
	 */
	@TableField(value = "update_time", fill = FieldFill.UPDATE)
	private Date updateTime;

	@TableField(value = "hospital_id", fill = FieldFill.INSERT)
	private Integer hospitalId;

}
