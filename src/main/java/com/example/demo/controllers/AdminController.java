package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/admin")
class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @GetMapping("/users")
    public String getUsers() {
        log.info("Admin users endpoint accessed");
        return "This is the admin page";
    }

    @GetMapping("/comments")
    public String getComments() {
        log.info("Admin comments endpoint accessed");
        return "This is the admin page";
    }
}
