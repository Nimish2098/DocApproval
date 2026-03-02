package com.Project.DocApproval.service;

import com.Project.DocApproval.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService {
    User addUser(User user);

    @Transactional
    User updateUser(UUID id, User user);

    @Transactional
    User patchUser(UUID id, Map<String, Object> updates);

    void deleteUser(UUID id);

    List<User> getAllUsers();
}
