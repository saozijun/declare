package com.cen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 专家分配表
 * </p>
 *
 * @author cen
 * @since 2024-07-06
 */
@Data
@TableName("expert_assignment")
public class ExpertAssignment {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目申报ID
     */
    private Long applicationId;

    /**
     * 专家用户ID
     */
    private Long expertId;

    /**
     * 分配状态：0-待评审 1-已评审
     */
    private String status;

    /**
     * 分配时间
     */
    private LocalDateTime assignTime;

    /**
     * 评审完成时间
     */
    private LocalDateTime reviewTime;
} 