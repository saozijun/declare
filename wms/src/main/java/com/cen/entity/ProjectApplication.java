package com.cen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目申报实体
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Data
@TableName("project_application")
public class ProjectApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 申报用户ID
     */
    private Long userId;

    /**
     * 申报状态：0-待审核，1-已通过，2-已拒绝，3-已分配
     */
    private String status;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 申报单位
     */
    private String organization;

    /**
     * 项目负责人
     */
    private String leader;

    /**
     * 项目周期
     */
    private String period;

    /**
     * 预算总额
     */
    private BigDecimal budget;

    /**
     * 技术方案
     */
    private String technicalSolution;

    /**
     * 预期成果
     */
    private String expectedResults;

    /**
     * 风险识别
     */
    private String riskIdentification;

    /**
     * 经济效益
     */
    private String economicBenefits;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 用户名称（非数据库字段）
     */
    @TableField(exist = false)
    private String userName;
} 