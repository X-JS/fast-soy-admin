package com.fastsoy.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fastsoy.common.dto.Result;
import com.fastsoy.common.dbo.UserDBO;
import com.fastsoy.common.context.UserContext;
import com.fastsoy.admin.dao.UserMapper;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public Result<Object> getUserById(@PathVariable Long id) {
        UserDBO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDBO>()
                        .eq(UserDBO::getId, id)
        );

        if (user == null) {
            return Result.fail("1101", "用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/current")
    public Result<Object> getCurrentUser() {
        UserDBO user = UserContext.get();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        return Result.success(user);
    }
}
