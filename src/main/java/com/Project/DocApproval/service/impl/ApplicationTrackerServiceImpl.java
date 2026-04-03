package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.enums.ResumeStatus;
import com.Project.DocApproval.exceptions.DuplicateApplicationException;
import com.Project.DocApproval.model.Application;
import com.Project.DocApproval.repository.ApplicationTrackerRepository;
import com.Project.DocApproval.service.ApplicationTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationTrackerServiceImpl implements ApplicationTrackerService {

    private final ApplicationTrackerRepository trackerRepository;

    public Application createApplication(Application application){
        if(!trackerRepository.existsById(application.getUuid())){
            throw  new DuplicateApplicationException();
        }
        return trackerRepository.save(application);
    }

    public void updateApplication(UUID applicationId,Application application){
       Application savedApplication =  trackerRepository.findById(applicationId)
               .orElseThrow(()-> new RuntimeException("Application Not Found"));


       savedApplication.setUrl(application.getUrl());
       savedApplication.setResumeStatus(application.getResumeStatus());
       savedApplication.setPosition(application.getPosition());
       savedApplication.setCompany(application.getCompany());
    }

    public void updateApplicationStatus(UUID id, ResumeStatus status){
        Application savedApplication = trackerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Application Not Found"));

        savedApplication.setResumeStatus(status);
    }
    public void partialUpdateApplication(UUID id, Map<String, Object>map){
        Application savedApplication = trackerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Application Not Found"));

        map.forEach((key,value)->
        {
            if(key=="url") savedApplication.setUrl((String)value);
            else if(key=="company") savedApplication.setCompany((String)value);
            else if(key=="position") savedApplication.setPosition((String)value);
            else if(key=="status") savedApplication.setResumeStatus((ResumeStatus) value);
        });

    }
}
