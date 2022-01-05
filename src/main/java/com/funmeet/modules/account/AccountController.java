package com.funmeet.modules.account;


import com.funmeet.modules.account.form.SignUpForm;
import com.funmeet.modules.account.security.CurrentAccount;
import com.funmeet.modules.account.validator.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final AccountDetailsService accountDetailsService;
    private final AccountEmailService accountEmailService; // 순참

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model){
        model.addAttribute("signUpForm",new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(@Valid @ModelAttribute SignUpForm signUpForm, Errors errors){
        if (errors.hasErrors()){
            return "account/sign-up";
        }
        accountService.processSignUpAccount(signUpForm);
        accountEmailService.sendSignUpConfirmEmail(signUpForm.getEmail());
        return "redirect:/";
    }

    @GetMapping("/certification-email")
    public String checkEmail(@CurrentAccount Account account, Model model) {
        model.addAttribute("email", account.getEmail());
        return "email/certification-email";
    }

    @GetMapping("/resend-email")
    public String resendConfirmEmail(@CurrentAccount Account account, Model model) {

        if (!account.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 30분에 한번만 전송할 수 있습니다.");
            model.addAttribute("email", account.getEmail());
            return "email/certification-email";
        }
        accountEmailService.sendSignUpConfirmEmail(account.getEmail());
        return "redirect:/";
    }

    @GetMapping("/find-account")
    public String emailLoginForm() {
        return "email/find-account";
    }

    @PostMapping("/find-account") // TODO: 2022-01-04 Entity -> Service
    public String sendEmailLoginLink(String email, Model model, RedirectAttributes attributes) {

        if (!accountService.canSendConfirmEmail(email)) {
            model.addAttribute("error", "시간");
            return "email/find-account";
        }

        accountEmailService.sendLoginLink(email);
        attributes.addFlashAttribute("message", "성공");
        return "redirect:/find-account";
    }

    @GetMapping("/check-email-token") // 이메일 토큰 눌렀을 때 뜨는
    public String checkEmailToken(String token, String email, Model model){

        if(!accountService.isValidToken(email, token)){
            model.addAttribute("error","wrong.token");
            return "email/check-email";
        }

        Account account = accountService.completeSignUp(email);
        model.addAttribute("nickname",account.getNickname());
        return "email/check-email";
    }

    @GetMapping("/auth-email")
    public String passByEmail(String token, String email, Model model) {

        if (!accountService.isValidToken(email, token)) {
            model.addAttribute("error", "로그인할 수 없습니다.");
            return "email/auth-email";
        }

        accountDetailsService.loginByEmail(email);
        return "email/auth-email";
    }

    @GetMapping("/profile/{nickname}")
    public String viewProfile(@PathVariable String nickname, Model model, @CurrentAccount Account account){

        Account viewAccount = accountService.findAccountByNickname(nickname);
        model.addAttribute("account",viewAccount);
        model.addAttribute("isOwner",viewAccount.equals(account));
        model.addAttribute("cityList",viewAccount.getCity());

        return "account/profile";
    }
}
