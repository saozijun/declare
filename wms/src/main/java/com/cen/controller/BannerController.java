package com.cen.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cen.common.Result;
import javax.annotation.Resource;
import java.util.List;

import com.cen.service.IBannerService;
import com.cen.entity.Banner;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author volcano
 * @since 2025-05-01
 */
@RestController
@RequestMapping("/banner")
public class BannerController {

    @Resource
    private IBannerService bannerService;
    //新增或修改
    @PostMapping("/save")
    public Result save(@RequestBody Banner banner) {
        return Result.success(bannerService.saveOrUpdate(banner));
    }

    //列表
    @GetMapping("/list")
    public Result list() {
        return Result.success(bannerService.list());
    }
    //删除
    @PostMapping("/delete")
    public Result delete(@RequestBody Banner banner){ //@RequestBody把前台的json对象转成java的对象
        return Result.success(bannerService.removeById(banner.getId()));
    }
    //批量删除
    @PostMapping("/del/batch")
    public Result Batch(@RequestBody List<Integer> ids){
        return Result.success(bannerService.removeBatchByIds(ids));
    }
    //根据id获取
    @GetMapping("/getById")
    public Result findOne(@PathVariable Banner banner) {
        return Result.success(bannerService.getById(banner.getId()));
    }
    //分页查询
    @GetMapping("/page")
    public Result findPage(@RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(Strings.isNotEmpty(name),"name",name);
        queryWrapper.orderByDesc("id"); //设置id倒序
        return Result.success(bannerService.page(new Page<>(pageNum, pageSize), queryWrapper));
    }
}

