package com.cen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目专家关联实体
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Data
@TableName("project_expert")
public class ProjectExpert implements Serializable {

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
     * 专家ID
     */
    private Long expertId;

    /**
     * 评价内容
     */
    private String review;

    /**
     * 评分
     */
    private Integer score;

    /**
     * 评审状态：0-未评审，1-已评审
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 专家姓名（非数据库字段）
     */
    @TableField(exist = false)
    private String expertName;

    /**
     * 项目名称（非数据库字段）
     */
    @TableField(exist = false)
    private String projectName;
} 