package com.Project.DocApproval.controller;

import com.Project.DocApproval.dto.ApplicationStatusRequest;
import com.Project.DocApproval.model.Application;
import com.Project.DocApproval.service.impl.ApplicationTrackerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application")
@RequiredArgsConstructor
public class ApplicationTrackerController {

    private final ApplicationTrackerServiceImpl trackerService;

    //Create a new Application
    @PostMapping
    public ResponseEntity<Application> addApplication(@RequestBody Application application){
        Application saved = trackerService.createApplication(application);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);

    }

    @PutMapping("/{id}")
    public ResponseEntity<Application> updateApplication(@PathVariable UUID id,@RequestBody Application application){
        return new ResponseEntity<>(trackerService.updateApplication(id,application),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateApplicationStatus(
            @PathVariable UUID id, @RequestBody ApplicationStatusRequest request
            ){
        return new ResponseEntity<>(request.getStatus()+"Updated Successfully",HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteApplication(
            @PathVariable UUID id
    ){
        return new ResponseEntity<>("Application Deleted Successfully",HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Application> updateApplication(
            @PathVariable UUID id, @RequestBody Map<String,Object>map
    ){
        return new ResponseEntity<>(trackerService.partialUpdateApplication(id,map),HttpStatus.OK);
    }
}
