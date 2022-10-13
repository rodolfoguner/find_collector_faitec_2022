package br.fai.findcollectors.findcollectorsclient.controller;


import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.findcollectorsclient.service.CityService;
import br.fai.findcollectors.findcollectorsclient.service.CollectService;
import br.fai.findcollectors.findcollectorsclient.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Autowired
    CityService cityService;


    @GetMapping("/")
    public String getListCollect(Model model) {

        List<Collect> collects = collectService.find();

        if (collects == null || collects.isEmpty()) {
            collects = new ArrayList<>();
        }

        model.addAttribute("collects", collects);

        return "collect/list";
    }

    @GetMapping("/edit/{id}")
    public String getCreatePage(@PathVariable final int id, Model model, HttpSession session) {

        Collect collect = collectService.findById(id);

        if (collect == null) {
            return "redirect:/common/not-found";
        }

        List<State> states = stateService.find();
        List<City> cities = cityService.findByStateId(collect.getCity().getStateId().getId());

        if (states == null || states.isEmpty()) {
            states = new ArrayList<>();
        }

        if (cities == null || cities.isEmpty()) {
            cities = new ArrayList<>();
        }

        Person person = (Person) session.getAttribute("currentUser");
        GarbageType[] garbageTypes = GarbageType.values();

        model.addAttribute("collect", collect);
        model.addAttribute("recyclerId", person.getId());
        model.addAttribute("garbageTypes", garbageTypes);
        model.addAttribute("states", states);
        model.addAttribute("cities", cities);

        return "collect/edit";
    }

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

        return "redirect:/collect/";
    }

}
