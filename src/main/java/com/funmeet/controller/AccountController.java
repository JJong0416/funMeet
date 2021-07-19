package com.funmeet.controller;

import com.funmeet.domain.Account;
import com.funmeet.form.SignUpForm;
import com.funmeet.repository.AccountRepository;
import com.funmeet.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;
    private final JavaMailSender javaMailSender;

    // signUp Validation Check
    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping({"","/"})
    public String Home(){
        return "index";
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signupForm",new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if (errors.hasErrors()){
            return "account/sign-up";
        }

        Account account = Account.builder()
                .nickname(signUpForm.getNickname())
                .email(signUpForm.getEmail())
                .password(signUpForm.getPassword()) // password Encording 해야한다.
                .meetCreatedByWeb(true)
                .meetEnrollmentResultByWeb(true)
                .meetUpdatedByWeb(true)
                .build();

        Account newAccount = accountRepository.save(account);

        newAccount.generateEmailCheckToken();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디 올래, 회원가입 인증");
        mailMessage.setText("/check-email-token?" + newAccount.getEmailCheckToken() +
                "&email=" + newAccount.getEmail());

        javaMailSender.send(mailMessage);
        return "redirect:/";
    }
}
