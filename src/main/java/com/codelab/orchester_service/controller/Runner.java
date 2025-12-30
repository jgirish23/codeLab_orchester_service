package com.codelab.orchester_service.controller;

import com.codelab.orchester_service.service.StartOrchestration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Runner {

    private final StartOrchestration startOrchestration;

    public Runner(StartOrchestration startOrchestration) {
        this.startOrchestration = startOrchestration;
    }


    @GetMapping("/start")
    public ResponseEntity<?> startOrchester(@RequestParam String id, @RequestParam String language){
        String temp = startOrchestration.startOrchestrationFromYml(id, language);
        System.out.println(temp);
        return ResponseEntity.ok("Filed saved Successfully");
    }

    @GetMapping("/stop")
    public ResponseEntity<?> stopOrchester(@RequestParam String id){
        String temp = startOrchestration.stopAndDeleteResources(id);
        System.out.println(temp);
        return ResponseEntity.ok("Stopped Successfully");
    }
}
