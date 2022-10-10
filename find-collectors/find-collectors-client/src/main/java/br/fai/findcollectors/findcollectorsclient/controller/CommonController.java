package br.fai.findcollectors.findcollectorsclient.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/common")
public class CommonController {

    @GetMapping("/not-found")
    public String getNotFoundPage() {
        return "common/not-found";
    }

    @GetMapping("/access-denied")
    public String getAccessDeniedPage() {
        return "common/access-denied";
    }

}
