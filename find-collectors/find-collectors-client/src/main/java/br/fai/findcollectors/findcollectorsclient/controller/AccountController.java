package br.fai.findcollectors.findcollectorsclient.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {

    @GetMapping("/sign-in")
    public String getLoginPage() {
        return "account/access";
    }

}
