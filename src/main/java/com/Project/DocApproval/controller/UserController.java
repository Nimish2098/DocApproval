package com.Project.DocApproval.controller;

import com.Project.DocApproval.model.User;
import com.Project.DocApproval.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user){
        return userService.addUser(user);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public User updateUser(@PathVariable Long id,@RequestBody User user){
        return userService.updateUser(id,user);
    }

    @PatchMapping("/{id}")
    public User patchUser(@PathVariable Long id, @RequestBody Map<String,Object>map){
        return userService.patchUser(id,map);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
    }


}
