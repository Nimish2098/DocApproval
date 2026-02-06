package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.Decision;
import com.Project.DocApproval.service.DecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/decision")
@RequiredArgsConstructor
public class DecisionController {

    private DecisionService decisionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Decision addDecision(@RequestBody Decision decision){
       return decisionService.addDecision(decision);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Decision updateDecision(@PathVariable Long id,@RequestBody Decision decision){
        return decisionService.updateDecision(id,decision);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteDecision(@PathVariable Long id){
        decisionService.deleteDecision(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Decision patchDecision(@PathVariable Long id,@RequestBody Map<String,Object> decision){
        return decisionService.patchDecision(id,decision);
    }

}
