package com.Project.DocApproval.service;

import com.Project.DocApproval.enums.RequestStatus;
import com.Project.DocApproval.model.Request;
import com.Project.DocApproval.model.RequestType;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository RequestRepository;

    public Request addRequest(Request request){
        return RequestRepository.save(request);
    }

    public void deleteRequest(Long id){
        RequestRepository.deleteById(id);
    }

    public Request updateRequest(Long id,Request request){
        Request existingRequest = RequestRepository.getRequestById(id);

        existingRequest.setRequestType(request.getRequestType());
        existingRequest.setStatus(request.getStatus());
        existingRequest.setPayload(request.getPayload());
        existingRequest.setCreatedAt(request.getCreatedAt());
        existingRequest.setCreatedBy(request.getCreatedBy());

        return RequestRepository.save(existingRequest);
    }

    public Request patchRequest(Long id, Map<String , Object>map){

        Request existingRequest = RequestRepository.getRequestById(id);

        map.forEach((key,value)->{
            switch (key){
                case "requestType" : existingRequest.setRequestType((RequestType) value); break;
                case "status" : existingRequest.setStatus((RequestStatus) value); break;
                case "payload" : existingRequest.setPayload((String) value); break;
                case "createdBy" : existingRequest.setCreatedBy((User) value); break;
            }
        });
        return RequestRepository.save(existingRequest);
    }
}