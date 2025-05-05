package com.cen.controller;

import com.cen.common.Result;
import com.cen.service.IExpertReviewService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 评审结果控制器
 * </p>
 *
 * @author cen
 * @since 2024-07-10
 */
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Resource
    private IExpertReviewService reviewService;
    
    /**
     * 获取项目申报的评审结果
     */
    @GetMapping("/result")
    public Result getReviewResult(@RequestParam Long applicationId) {
        return Result.success(reviewService.getReviewResultByApplicationId(applicationId));
    }
} 