package com.seregamorph.restapi.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    static final String SWAGGER_UI_REDIRECT = "redirect:swagger-ui.html";

    @GetMapping
    String doGet() {
        return SWAGGER_UI_REDIRECT;
    }
}
