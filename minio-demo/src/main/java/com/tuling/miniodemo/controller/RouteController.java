package com.tuling.miniodemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Fox
 */
@Controller
public class RouteController {

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping({"/upload.html"})
    public String upload() {
        return "upload";
    }
}
