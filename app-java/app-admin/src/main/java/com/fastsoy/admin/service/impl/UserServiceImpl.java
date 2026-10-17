package com.fastsoy.admin.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.fastsoy.admin.dao.UserMapper;
import com.fastsoy.admin.service.IUserService;
import com.fastsoy.common.dbo.UserDBO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDBO> implements IUserService {

}