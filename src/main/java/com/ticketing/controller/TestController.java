package com.ticketing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/simple")
    public String simpleTest(Model model) {
        model.addAttribute("message", "Ceci est un test simple");
        return "test/simple";
    }
}
