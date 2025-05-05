package com.cen.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("expert")
public class Expert {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;  // 关联用户ID
    
    private String name;     // 专家姓名
    private String gender;   // 性别
    private String phone;    // 联系方式
    private String email;    // 电子邮箱
    private String qualification;  // 资质证明
    private String expertise;      // 专业领域
    private String education;      // 教育背景
    private String workExperience; // 工作经验
    private String achievements;   // 主要成就
    private String status;         // 状态：审核中/已通过/已拒绝
} 