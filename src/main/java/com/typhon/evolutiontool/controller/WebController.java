package com.typhon.evolutiontool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class WebController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String renderHomePage() {
        return "homePage";
    }
}
