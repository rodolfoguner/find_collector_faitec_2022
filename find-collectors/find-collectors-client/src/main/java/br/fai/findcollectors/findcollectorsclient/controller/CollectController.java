package br.fai.findcollectors.findcollectorsclient.controller;


import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.findcollectorsclient.service.CollectService;
import br.fai.findcollectors.findcollectorsclient.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    CollectService<Collect> collectService;
    @Autowired
    StateService stateService;

    @GetMapping("/create")
    public String getCreatePage(Model model, HttpSession session) {

        List<State> states = stateService.find();

        if (states == null || states.isEmpty()) {
            states = new ArrayList<>();
        }

        Person person = (Person) session.getAttribute("currentUser");
        GarbageType[] garbageTypes = GarbageType.values();

        model.addAttribute("recyclerId", person.getId());
        model.addAttribute("garbageTypes", garbageTypes);
        model.addAttribute("states", states);

        return "collect/create";
    }

    @PostMapping("/create")
    public String createCollect(Collect collect) {


        int collectId = collectService.create(collect);

        if (collectId <= 0) {
            return "redirect/common/not-found";
        }

        return "redirect:/";
    }

}
