package com.cen.controller;

import com.cen.common.Result;
import com.cen.entity.Expert;
import com.cen.service.IExpertService;
import com.cen.service.IUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/expert")
public class ExpertController {

    @Resource
    private IExpertService expertService;
    
    @Resource
    private IUserService userService;

    @PostMapping("/save")
    public Result save(@RequestBody Expert expert) {
        return expertService.saveExpert(expert);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Expert expert) {
        return expertService.updateExpert(expert);
    }

    @GetMapping("/{userId}")
    public Result getByUserId(@PathVariable Integer userId) {
        return expertService.getExpertByUserId(userId);
    }

    @GetMapping("/list")
    public Result list() {
        return expertService.getExpertList();
    }

    @PostMapping("/status/{id}")
    public Result updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return expertService.updateExpertStatus(id, status);
    }
    
    @GetMapping("/available-users")
    public Result getAvailableUsers() {
        return userService.getAvailableUsersForExpert();
    }
} 