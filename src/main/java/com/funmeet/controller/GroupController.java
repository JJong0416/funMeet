package com.funmeet.controller;

import com.funmeet.annotation.CurrentAccount;
import com.funmeet.domain.Account;
import com.funmeet.domain.Group;
import com.funmeet.form.GroupForm;
import com.funmeet.service.GroupService;
import com.funmeet.validator.GroupFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final ModelMapper modelMapper;
    private final GroupFormValidator groupFormValidator;

    @InitBinder("groupForm")
    public void groupFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(groupFormValidator);
    }

    @GetMapping("/create_group")
    public String createGroup(@CurrentAccount Account account, Model model){
        model.addAttribute(account);
        model.addAttribute("groupForm",new GroupForm());
        return "group/form";
    }

    @PostMapping("/create_group")
    public String createGroup(@CurrentAccount Account account, @Valid GroupForm groupForm, Errors errors){
        if (errors.hasErrors()){
            return "group/form";
        }

        Group createGroup = groupService.createGroup(modelMapper.map(groupForm,Group.class),account);
        return "redirect:/group/" + URLEncoder.encode(createGroup.getGroupPath(), StandardCharsets.UTF_8);
    }
}
