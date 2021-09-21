package com.funmeet.modules.account.oauth;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.AccountRepository;
import com.funmeet.modules.account.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OAuthController {

    private final AccountRepository accountRepository;
    private final OAuthService oAuthService;
    private final AccountService accountService;
    private final OAuthFormValidator oAuthFormValidator;

    @InitBinder("oAuthForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(oAuthFormValidator);
    }

    @GetMapping("/oauth2/authorization/kakao")
    public String kakaoCallback(@RequestParam String code, Model model, HttpServletRequest request){


        OAuthToken oAuthToken = oAuthService.getAccessToken(code);
        KakaoProfile kakaoProfile = oAuthService.getProfile(oAuthToken);

        String kakaoEmail = kakaoProfile.kakao_account.email;
        Account account = oAuthService.findAccountByKakaoEmail(kakaoEmail);

        if (account == null){
            model.addAttribute("oauthForm",new OAuthForm());
            model.addAttribute("kakaoEmail",kakaoEmail);
            return "account/oauth_sign_up";
        }

        accountService.login(account);
        return "index";
    }

    @PostMapping("/oauth_sign_up")
    public String signUpSubmit(@Valid @ModelAttribute OAuthForm oAuthForm, Errors errors,String kakaoEmail){
        if (errors.hasErrors()){
            return "account/oauth_sign_up";
        }
        Account account = accountService.oauthSignUp(oAuthForm,kakaoEmail);
        accountService.login(account);
        return "index";
    }
}
