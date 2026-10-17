package com.fastsoy.admin.controller;

import com.fastsoy.admin.service.IUserService;
import com.fastsoy.common.dbo.UserDBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.json.JsonFactory;

@RestController
public class HelloController {

    @Autowired
    IUserService userService;

    @GetMapping("/hello")
    public String hello() {
        UserDBO user = userService.getById(1);
        return "user: " + user.getId() + ", " + user.getUserName() + ", " + user.getNickName();
    }

}
