package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.Application;
import com.Project.DocApproval.service.impl.ApplicationTrackerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/application")
@RequiredArgsConstructor
public class ApplicationTrackerController {

    private final ApplicationTrackerServiceImpl trackerService;

    //Create new Application
    @PostMapping
    public ResponseEntity<Application> addApplication(@RequestBody Application application){
        Application saved = trackerService.createApplication(application);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);

    }


}
