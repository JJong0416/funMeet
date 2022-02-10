package com.funmeet.modules.alarm;

import com.funmeet.modules.account.Account;
import com.funmeet.modules.account.security.AdaptAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AlarmInterceptor implements HandlerInterceptor {

    private final AlarmRepository alarmRepository;

    /* afterCompletion은 뷰 렌더링 이후, postHandle은 뷰 렌더링 전, 핸들러 처리 이후*/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        /* modelAndView를 안쓰면서, Redirect 뷰타입이 아니면서 authentication이 null 아니며 AdaptAccountType만  */
        if (modelAndView != null && !isRedirectView(modelAndView) && authentication != null
                && authentication.getPrincipal() instanceof AdaptAccount) {
            Account account = ((AdaptAccount) authentication.getPrincipal()).getAccount();
            long count = alarmRepository.countByAccountAndChecked(account, false);
            modelAndView.addObject("alarmMessage", count > 0);
        }
    }

    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }

}
