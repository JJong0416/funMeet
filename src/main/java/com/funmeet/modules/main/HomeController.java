package com.funmeet.modules.main;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping({"","/"})
    public String home(@CurrentAccount Account account, Model model){
        if (account != null){
            model.addAttribute(account);
        }
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }
}