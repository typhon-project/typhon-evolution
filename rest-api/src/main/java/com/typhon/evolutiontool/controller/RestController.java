package com.typhon.evolutiontool.controller;

import com.typhon.evolutiontool.EvolutionTool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private EvolutionTool evolutionTool = new EvolutionTool();

    @RequestMapping(value = "/evolve")
    public String postSmo(@RequestParam("initialModelPath") String initialModelPath, @RequestParam("finalModelPath") String finalModelPath) {
        return evolutionTool.evolve(initialModelPath, finalModelPath);
    }

}
