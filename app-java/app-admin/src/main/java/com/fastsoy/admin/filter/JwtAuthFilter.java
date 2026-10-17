package com.fastsoy.admin.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fastsoy.common.context.UserContext;
import com.fastsoy.common.dto.Result;
import com.fastsoy.common.dbo.UserDBO;
import com.fastsoy.common.jwt.JwtUtils;
import com.fastsoy.admin.dao.UserMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

public class JwtAuthFilter implements Filter {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /** 公开路径（无需认证） */
    private static final List<String> PERMIT_ALL = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/register",
            "/api/v1/auth/captcha",
            "/api/v1/auth/code-login",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/error",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    );

    public JwtAuthFilter(JwtUtils jwtUtils, UserMapper userMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }

    @Override
    public void doFilter(jakarta.servlet.ServletRequest servletRequest,
                         jakarta.servlet.ServletResponse servletResponse,
                         FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getServletPath();

        // 非 /api/ 路径（前端路由 /login /403 / 等）直接放行
        if (!path.startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 白名单路径直接放行
        if (PERMIT_ALL.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            filterChain.doFilter(request, response);
            return;
        }

        // 认证检查
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, Result.unauthorized("认证失败，请求中不存在令牌"));
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            sendUnauthorized(response, Result.unauthorized("登录已过期，请重新登录"));
            return;
        }

        String tokenType = jwtUtils.getTokenTypeFromToken(token);
        if (!"accessToken".equals(tokenType)) {
            sendUnauthorized(response, Result.unauthorized("该令牌不是访问令牌"));
            return;
        }

        Long userId = jwtUtils.getUserIdFromToken(token);
        UserDBO user = userMapper.selectOne(
                new LambdaQueryWrapper<UserDBO>()
                        .eq(UserDBO::getId, userId)
        );

        if (user == null) {
            sendUnauthorized(response, Result.unauthorized("认证失败，用户不存在"));
            return;
        }

        if (user.getStatusType() != null && user.getStatusType() == 0) {
            sendUnauthorized(response, Result.accountDisabled());
            return;
        }

        UserContext.set(user);
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private void sendUnauthorized(HttpServletResponse response, Result<?> result) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"code\":\"" + result.getCode()
                + "\",\"msg\":\"" + result.getMsg()
                + "\",\"data\":null}";
        response.getWriter().write(json);
    }
}
