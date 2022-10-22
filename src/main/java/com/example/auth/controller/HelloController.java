package com.example.auth.controller;

import com.example.auth.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/hello")
    public String sayHello(@RequestHeader(name = "Authorization") String authorization) {
        final String username = jwtTokenUtil.getUsernameFromToken(authorization.substring(7));
        return "Hello " + username + "!";
    }
}
