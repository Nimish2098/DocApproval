package com.Project.DocApproval.service;

import com.Project.DocApproval.enums.Role;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(Long id,User user){
        User existingUser;
        try {
            existingUser = userRepository.getUserById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        return userRepository.save(existingUser);
    }

    public User patchUser(Long id, Map<String,Object> updates){
        User existingUser = userRepository.getUserById(id);

        updates.forEach((key,value)->{
            switch (key){
                case "username":existingUser.setUsername((String) value); break;
                case "email":   existingUser.setEmail((String)value); break;
                case "password":existingUser.setPassword((String)value); break;
                case "Role":    existingUser.setRole((Role)value); break;
            }
        });
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
