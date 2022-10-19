package br.fai.findcollectors.findcollectorsclient.controller;

import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.findcollectorsclient.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CollectService<Collect> collectService;

    @GetMapping("/")
    public String getHomePage(Model model, HttpSession session) {

        Person person = (Person) session.getAttribute("currentUser");

        if (person != null && person.getCity() == null) {
            return "redirect:/account/edit-profile";
        }
        
        return "index";

    }
}
