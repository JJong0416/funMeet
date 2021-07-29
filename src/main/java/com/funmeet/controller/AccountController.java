package com.funmeet.controller;


import com.funmeet.annotation.CurrentAccount;
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
import org.springframework.web.bind.annotation.*;

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

    /* sign_up */


    @GetMapping("/sign_up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign_up";
    }

    @PostMapping("/sign_up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if (errors.hasErrors()){
            return "account/sign_up";
        }
        Account account = accountService.processSignUpAccount(signUpForm);
        accountService.login(account);
        return "redirect:/";
    }

    /* 이메일  시작 */

    @GetMapping("/certification_email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "email/certification_email";
    }

    @GetMapping("/resend_email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {
        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 1시간에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "email/certification_email";
        }

        accountService.sendSignUpConfirmEmail(account);
        return "redirect:/";
    }

    @GetMapping("/check_email_token") // 이메일 토큰 눌렀을 때 뜨는
    public String checkEmailToken(String token, String email, Model model){

        Account account = accountRepository.findByEmail(email);
        String view_url = "email/check_email";

        if (account == null){
            model.addAttribute("error","wrong.email");
            return view_url;
        }

        if(!account.isValidToken(token)){
            model.addAttribute("error","wrong.token");
            return view_url;
        }

        accountService.completeSignUp(account);
        model.addAttribute("nickname",account.getNickname());
        return view_url;
    }

    /* 이메일  마지막 */

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account){
        Account viewAccount = accountRepository.findByNickname(nickname);
        if (viewAccount == null){
            throw new IllegalArgumentException(nickname + "에 해당하는 사용자가 없습니다");
        }

        model.addAttribute("account",viewAccount);
        model.addAttribute("isOwner",viewAccount.equals(account));
        return "account/profile";
    }



}
