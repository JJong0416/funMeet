package com.funmeet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping({"","/"})
    public String Home(Model model){
        return "index";
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        return "account/sign-up";
    }
}
