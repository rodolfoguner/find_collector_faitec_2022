package br.fai.findcollectors.findcollectorsclient.controller;


import br.fai.findcollectors.entities.Collect;
import br.fai.findcollectors.entities.State;
import br.fai.findcollectors.findcollectorsclient.service.CollectService;
import br.fai.findcollectors.findcollectorsclient.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/collect")
public class CollectController {

    @Autowired
    CollectService<Collect> collectCollectService;
    @Autowired
    StateService stateService;

    @GetMapping("/create")
    public String getCreatePage(Model model) {

        List<State> states = stateService.find();

        if (states == null || states.isEmpty()) {
            states = new ArrayList<>();
        }

        model.addAttribute("states", states);

        return "collect/create";
    }

}
