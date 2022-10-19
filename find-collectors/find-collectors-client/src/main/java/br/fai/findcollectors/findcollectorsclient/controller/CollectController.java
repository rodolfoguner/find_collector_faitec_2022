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

    @Autowired
    HttpSession session;

    @GetMapping("/")
    public String getListCollect(Model model) {

        Person person = (Person) session.getAttribute("currentUser");

        if (person == null) {
            return "redirect:/common/not-found";
        }

        List<Collect> collects = collectService.myCollects(person.getId());

        if (collects == null || collects.isEmpty()) {
            collects = new ArrayList<>();
        }

        model.addAttribute("collects", collects);

        return "collect/list";
    }

    @GetMapping("/edit/{id}")
    public String getCreatePage(@PathVariable final int id, Model model) {

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
        model.addAttribute("currentUser", person.getId());
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

        model.addAttribute("currentUser", person.getId());
        model.addAttribute("garbageTypes", garbageTypes);
        model.addAttribute("states", states);

        return "collect/create";
    }

    @PostMapping("/create")
    public String createCollect(final Collect collect) {

        int collectId = collectService.create(collect);

        if (collectId <= 0) {
            return "redirect:/common/not-found";
        }

        return "redirect:/collect/";
    }

    @PostMapping("/update")
    public String updateCollect(final Collect collect) {

        boolean updated = collectService.update(collect.getId(), collect);

        if (!updated) {
            return "redirect:/common/not-found";
        }

        return "redirect:/collect/";
    }

    @GetMapping("/delete/{id}")
    public String deleteCollect(@PathVariable final int id) {
        boolean deleted = collectService.deleteById(id);

        if (!deleted) {
            return "redirect:/common/not-found";
        }

        return "redirect:/collect/";
    }

    @PostMapping("/accept-collect")
    public String acceptCollect(final Collect collect) {

        boolean accepted = collectService.acceptCollect(collect.getId(), collect);

        if (!accepted) {
            return "redirect:/common/not-found";
        }

        return "redirect:/collect/available-collects";
    }

    @GetMapping("/available-collects")
    public String availableCollects(Model model) {

        Person loggedPerson = (Person) session.getAttribute("currentUser");

        if (loggedPerson == null) {
            return "redirect:/common/not-found";
        }

        List<Collect> freeCollects = collectService.findFreeCollects(loggedPerson.getId());

        if (freeCollects == null || freeCollects.isEmpty()) {
            freeCollects = new ArrayList<>();
        }

        model.addAttribute("freeCollects", freeCollects);

        return "collect/available-collects";
    }

    @GetMapping("/accepted-collects")
    public String getAcceptedCollects(Model model) {
        Person person = (Person) session.getAttribute("currentUser");

        if (person == null) {
            return "redirect:/common/not-found";
        }

        List<Collect> collects = collectService.acceptedCollects(person.getId());

        if (collects == null || collects.isEmpty()) {
            collects = new ArrayList<>();
        }

        model.addAttribute("collects", collects);

        return "collect/accepted-collects";
    }

    @PostMapping("/close-collect")
    public String closeCollect(Collect collect) {

        boolean closed = collectService.closeCollect(collect.getId(), collect);

        if (!closed) {
            return "redirect:/common/not-found";
        }

        return "redirect:/collect/accepted-collects";
    }

}
