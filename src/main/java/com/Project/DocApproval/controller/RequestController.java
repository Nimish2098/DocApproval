package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.Request;
import com.Project.DocApproval.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/Request")
@RequiredArgsConstructor
public class RequestController {

    private RequestService RequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Request addRequest(@RequestBody Request Request){
       return RequestService.addRequest(Request);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Request updateRequest(@PathVariable Long id,@RequestBody Request Request){
        return RequestService.updateRequest(id,Request);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteRequest(@PathVariable Long id){
        RequestService.deleteRequest(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Request patchRequest(@PathVariable Long id,@RequestBody Map<String,Object> Request){
        return RequestService.patchRequest(id,Request);
    }

}
