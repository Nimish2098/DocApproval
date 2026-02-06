package com.Project.DocApproval.controller;


import com.Project.DocApproval.model.RequestType;
import com.Project.DocApproval.model.RequestType;
import com.Project.DocApproval.service.RequestTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/request_type")
@RequiredArgsConstructor
public class RequestTypeController {
    
    private RequestTypeService requestTypeService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestType addRequestType(@RequestBody RequestType requestType){
        return requestTypeService.addRequestType(requestType);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RequestType updateRequestType(@PathVariable Long id,@RequestBody RequestType requestType){
        return requestTypeService.updateRequestType(id,requestType);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteRequestType(@PathVariable Long id){
        requestTypeService.deleteRequestType(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RequestType patchRequestType(@PathVariable Long id,@RequestBody Map<String,Object> requestType){
        return requestTypeService.patchUpdate(id,requestType);
    }
}
