package br.fai.findcollectors.findcollectorsclient.controller;


import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.findcollectorsclient.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/godfather")
public class GodFatherController {

    @Autowired
    PersonService<Person> personService;

    @GetMapping("/")
    public String getCollectors(Model model, HttpSession session) {

        Person person = (Person) session.getAttribute("currentUser");

        if (person == null) {
            return "redirect:/common/not-found";
        }

        List<Person> collectors = personService.godfatherCollectors(person.getId());

        if (collectors == null || collectors.isEmpty()) {
            collectors = new ArrayList<>();
        }

        model.addAttribute("collectors", collectors);

        return "godfather/list";
    }

}
