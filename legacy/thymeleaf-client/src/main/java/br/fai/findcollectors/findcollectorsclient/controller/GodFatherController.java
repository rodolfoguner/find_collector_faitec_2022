package br.fai.findcollectors.findcollectorsclient.controller;


import br.fai.findcollectors.entities.City;
import br.fai.findcollectors.entities.Person;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.findcollectorsclient.service.CityService;
import br.fai.findcollectors.findcollectorsclient.service.PersonService;
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
@RequestMapping("/godfather")
public class GodFatherController {

    @Autowired
    PersonService<Person> personService;

    @Autowired
    HttpSession session;

    @Autowired
    StateService stateService;

    @Autowired
    CityService cityService;

    @GetMapping("/")
    public String getCollectors(Model model) {

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

    @GetMapping("/create")
    public String getCreateCollectorPage(Model model) {
        Person godFather = (Person) session.getAttribute("currentUser");

        if (godFather == null) {
            return "redirect:/common/not-found";
        }

        List<State> states = stateService.find();

        if (states == null || states.isEmpty()) {
            states = new ArrayList<>();
        }

        model.addAttribute("collector", new Person());
        model.addAttribute("godfatherId", godFather.getId());
        model.addAttribute("garbageTypes", GarbageType.values());
        model.addAttribute("personTypes", PersonType.values());
        model.addAttribute("states", states);

        return "godfather/create";
    }

    @PostMapping("/")
    public String godfatherCollector(Person person) {

        Person godfather = (Person) session.getAttribute("currentUser");

        int created = personService.godfather(person, godfather);

        if (created <= 0) {
            return "redirect:/common/not-found";
        }
        return "redirect:/godfather/";
    }

    @GetMapping("/edit/{id}")
    public String getGodfatherCollectorPage(@PathVariable("id") final int id, Model model) {

        Person collector = personService.findById(id);
        Person godfather = (Person) session.getAttribute("currentUser");

        if (collector == null || godfather == null) {
            return "redirect:/common/not-found";
        }

        List<State> states = stateService.find();

        if (states == null || states.isEmpty()) {
            states = new ArrayList<>();
        }

        List<City> cities = new ArrayList<>();
        if (godfather.getCityId() > 0) {
            cities = cityService.findByStateId(godfather.getCity().getStateId().getId());
        }

        model.addAttribute("collector", collector);
        model.addAttribute("godfatherId", godfather.getId());
        model.addAttribute("garbageTypes", GarbageType.values());
        model.addAttribute("personTypes", PersonType.values());
        model.addAttribute("states", states);
        model.addAttribute("cities", cities);

        return "godfather/edit";
    }

    @PostMapping("/update")
    public String getUpdateCollectorPage(Person person) {

        boolean updated = personService.updateGodfather(person.getId(), person);

        if (!updated) {
            return "redirect:/common/not-found";
        }
        return "redirect:/godfather/";
    }

}
