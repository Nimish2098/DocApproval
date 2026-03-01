package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.User;
import com.Project.DocApproval.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User request){
        return userService.addUser(request);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser(@PathVariable UUID id,@RequestBody User user){
        return userService.updateUser(id,user);
    }

    @PatchMapping("/{id}")
    public User patchUser(@PathVariable UUID id, @RequestBody Map<String,Object>map){
        return userService.patchUser(id,map);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
}
