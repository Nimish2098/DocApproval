package com.Project.DocApproval.application.service;

import com.Project.DocApproval.application.dto.ApplicationRequest;
import com.Project.DocApproval.application.dto.ApplicationResponse;
import com.Project.DocApproval.application.dto.ApplicationStatusRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface ApplicationTrackerService {
    ApplicationResponse createApplication(ApplicationRequest request, UserDetails userDetails);
    List<ApplicationResponse> getApplications(UserDetails userDetails);
    ApplicationResponse updateApplication(UUID id, ApplicationRequest request, UserDetails userDetails);
    ApplicationResponse updateApplicationStatus(UUID id, ApplicationStatusRequest request, UserDetails userDetails);
    void deleteApplication(UUID id, UserDetails userDetails);
}
