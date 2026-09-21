package com.Project.DocApproval.application.service.impl;

import com.Project.DocApproval.application.dto.ApplicationRequest;
import com.Project.DocApproval.application.dto.ApplicationResponse;
import com.Project.DocApproval.application.dto.ApplicationStatusRequest;
import com.Project.DocApproval.user.entity.User;
import com.Project.DocApproval.application.entity.Application;
import com.Project.DocApproval.application.repository.ApplicationTrackerRepository;
import com.Project.DocApproval.user.repository.UserRepository;
import com.Project.DocApproval.application.service.ApplicationTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationTrackerServiceImpl implements ApplicationTrackerService {

    private final ApplicationTrackerRepository trackerRepository;
    private final UserRepository userRepository;

    @Override
    public ApplicationResponse createApplication(ApplicationRequest request, UserDetails userDetails) {
        User user = getUser(userDetails);
        Application application = Application.builder()
                .user(user)
                .position(request.getPosition())
                .company(request.getCompany())
                .applicationStatus(request.getApplicationStatus())
                .localDateTime(LocalDateTime.now())
                .url(request.getUrl())
                .build();
        return toResponse(trackerRepository.save(application));
    }

    @Override
    public List<ApplicationResponse> getApplications(UserDetails userDetails) {
        User user = getUser(userDetails);
        return trackerRepository.findByUserOrderByLocalDateTimeDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ApplicationResponse updateApplication(UUID id, ApplicationRequest request, UserDetails userDetails) {
        Application application = getOwnedApplication(id, userDetails);
        application.setPosition(request.getPosition());
        application.setCompany(request.getCompany());
        application.setApplicationStatus(request.getApplicationStatus());
        application.setUrl(request.getUrl());
        return toResponse(trackerRepository.save(application));
    }

    @Override
    public ApplicationResponse updateApplicationStatus(UUID id, ApplicationStatusRequest request,
                                                        UserDetails userDetails) {
        Application application = getOwnedApplication(id, userDetails);
        application.setApplicationStatus(request.getStatus());
        return toResponse(trackerRepository.save(application));
    }

    @Override
    public void deleteApplication(UUID id, UserDetails userDetails) {
        trackerRepository.delete(getOwnedApplication(id, userDetails));
    }

    private User getUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Application getOwnedApplication(UUID id, UserDetails userDetails) {
        User user = getUser(userDetails);
        return trackerRepository.findByUuidAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private ApplicationResponse toResponse(Application application) {
        return new ApplicationResponse(
                application.getUuid(),
                application.getPosition(),
                application.getCompany(),
                application.getApplicationStatus(),
                application.getLocalDateTime(),
                application.getUrl());
    }
}
