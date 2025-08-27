package com.example.controllers;

import com.example.dtos.CreateUserRequest;
import com.example.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/add")
    public void addUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        this.userService.addUser(createUserRequest.toUser());
    }
}
