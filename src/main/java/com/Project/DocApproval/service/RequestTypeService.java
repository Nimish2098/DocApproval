package com.Project.DocApproval.service;

import com.Project.DocApproval.model.RequestType;
import com.Project.DocApproval.repository.RequestTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestTypeService {

    private final RequestTypeRepository repository;

    public RequestType addRequestType(RequestType RequestType){
        return repository.save(RequestType);
    }

    public void deleteRequestType(Long id){
        repository.deleteById(id);
    }

    public RequestType updateRequestType(Long id, RequestType requestType){
        RequestType existingRequestType = repository.getRequestTypeById(id);

        existingRequestType.setName(requestType.getName());
        existingRequestType.setSchema(requestType.getSchema());

        return repository.save(existingRequestType);
    }

    public RequestType patchUpdate(Long id, Map<String,Object> request){
        RequestType existingRequestType = repository.getRequestTypeById(id);

        request.forEach((key,value)->
        {
            switch (key){
                case "name" : existingRequestType.setName((String)value); break;
                case "schema" : existingRequestType.setSchema((String)value); break;
            }
        });

        return repository.save(existingRequestType);
    }
}
