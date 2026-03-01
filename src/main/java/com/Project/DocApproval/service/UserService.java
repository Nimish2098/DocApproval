package com.Project.DocApproval.service;

import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User addUser(User user){
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User user){
        User existingUser;
        try {
            existingUser = userRepository.getUserById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    public User patchUser(UUID id, Map<String,Object> updates){
        User existingUser = userRepository.getUserById(id);

        updates.forEach((key,value)->{
            switch (key){
                case "username":existingUser.setUsername((String) value); break;
                case "email":   existingUser.setEmail((String)value); break;
            }
        });
        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID id){
        userRepository.deleteById(id);
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

}
