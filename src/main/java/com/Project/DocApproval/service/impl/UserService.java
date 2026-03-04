package com.Project.DocApproval.service.impl;

import com.Project.DocApproval.exceptions.ResourceNotFoundException;
import com.Project.DocApproval.model.User;
import com.Project.DocApproval.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements com.Project.DocApproval.service.UserService {

    private final UserRepository userRepository;

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(UUID id, User user) {
        // Handle "Not Found" using your custom exception
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        existingUser.setName(user.getUsername());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    @Transactional
    @Override
    public User patchUser(UUID id, Map<String, Object> updates) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "username": existingUser.setName((String) value); break;
                case "email": existingUser.setEmail((String) value); break;
            }
        });
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete: User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}