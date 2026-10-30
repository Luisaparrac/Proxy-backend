package com.luisaparra.aiproxy.controller;

import com.luisaparra.aiproxy.dto.UserSummaryResponse;
import com.luisaparra.aiproxy.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lists demo users for the frontend selector.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserSummaryResponse> findAll() {
        return userService.findAll();
    }
}
