package com.fastsoy.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fastsoy.common.dto.LoginRequest;
import com.fastsoy.common.dto.LoginResponse;
import com.fastsoy.common.dto.Result;
import com.fastsoy.common.dbo.UserDBO;
import com.fastsoy.common.jwt.JwtUtils;
import com.fastsoy.admin.dao.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserMapper userMapper, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        UserDBO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDBO>()
                        .eq(UserDBO::getUserName, request.getUserName())
        );

        if (user == null) {
            return Result.credentialsError();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return Result.credentialsError();
        }

        if (user.getStatusType() != null && user.getStatusType() == 0) {
            return Result.accountDisabled();
        }

        Integer tokenVersion = user.getTokenVersion() != null ? user.getTokenVersion() : 0;

        String accessToken = jwtUtils.generateAccessToken(user.getId(), tokenVersion);
        String refreshToken = jwtUtils.generateRefreshToken(user.getId(), tokenVersion);

        LoginResponse data = new LoginResponse(accessToken, refreshToken);
        return Result.success(data);
    }

    @PostMapping("/refresh-token")
    public Result<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            return Result.unauthorized("登录已过期，请重新登录");
        }

        String tokenType = jwtUtils.getTokenTypeFromToken(refreshToken);
        if (!"refreshToken".equals(tokenType)) {
            return Result.unauthorized("该令牌不是刷新令牌");
        }

        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        UserDBO user = userMapper.selectById(userId);
        if (user == null) {
            return Result.unauthorized("用户不存在");
        }
        if (user.getStatusType() != null && user.getStatusType() == 0) {
            return Result.accountDisabled();
        }

        Integer tokenVersion = user.getTokenVersion() != null ? user.getTokenVersion() : 0;
        Integer tokenVersionInJwt = jwtUtils.getTokenVersionFromToken(refreshToken);
        if (tokenVersionInJwt < tokenVersion) {
            return Result.sessionInvalidated();
        }

        String newAccessToken = jwtUtils.generateAccessToken(user.getId(), tokenVersion);
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getId(), tokenVersion);

        LoginResponse data = new LoginResponse(newAccessToken, newRefreshToken);
        return Result.success(data);
    }

    @GetMapping("/user-info")
    public Result<Object> getUserInfo() {
        UserDBO user = com.fastsoy.common.context.UserContext.get();
        if (user == null) {
            return Result.unauthorized("未登录");
        }
        return Result.success(user);
    }
}
