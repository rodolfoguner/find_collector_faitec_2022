package br.fai.findcollectors.findcollectorsclient.controller;


import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsclient.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/account")
public class AccountController {

    @Autowired
    PersonService<Person> personService;

    @GetMapping("/sign-in")
    public String getLoginPage() {
        return "account/sign-in-page";
    }

    @GetMapping("/sign-up")
    public String getSignUpPage() {
        return "account/sign-up-page";
    }

    @GetMapping("/edit-profile")
    public String getEditProfilePage(final Model model, final HttpSession session) {

        Person person = (Person) session.getAttribute("currentUser");

        if (person == null) {
            return "redirect:/common/not-found";
        }

        model.addAttribute("currentUser", person);

        return "account/edit-profile";
    }

    @PostMapping("/sign-up")
    public String createLogin(Person person) {

        int created = personService.create(person);

        if (created <= 0) {
            return "redirect:/common/access-denied";
        }

        return "redirect:/account/sign-in";
    }

}
