package br.fai.findcollectors.findcollectorsclient.controller;

import br.fai.findcollectors.entities.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHomePage(HttpSession session) {

        Person person = (Person) session.getAttribute("currentUser");

        if (person != null && person.getCity() == null) {
            return "redirect:/account/edit-profile";
        }

        return "index";
    }
}
