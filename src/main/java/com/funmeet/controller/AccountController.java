package com.funmeet.controller;


import com.funmeet.domain.Account;
import com.funmeet.form.SignUpForm;
import com.funmeet.repository.AccountRepository;
import com.funmeet.service.AccountService;
import com.funmeet.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountRepository accountRepository;


    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    /* GetMapping Method */

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }

    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model){

        String view_url = "account/check_email";

        Account account = accountRepository.findByEmail(email);
        if (account == null){
            model.addAttribute("error","wrong.email");
            return view_url;
        }

        if(!account.getEmailCheckToken().equals(token)){
            model.addAttribute("error","wrong.token");
            return view_url;
        };

        account.completeSignUp();
        accountService.login(account);
        model.addAttribute("nickname",account.getNickname());

        return view_url;
    }

    /* Post Mapping Method */

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if (errors.hasErrors()){
            return "account/sign-up";
        }
        Account account = accountService.processSignUpAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }


}
