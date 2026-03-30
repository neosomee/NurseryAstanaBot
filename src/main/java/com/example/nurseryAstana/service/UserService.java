package com.example.nurseryAstana.service;

import com.example.nurseryAstana.model.User;
import com.example.nurseryAstana.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByTelegramId(Long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    @Transactional
    public User createOrGetUser(Long telegramId, String username) {
        Optional<User> existing = findByTelegramId(telegramId);
        if (existing.isPresent()) {
            return existing.get();
        }
        User user = new User(telegramId, username);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
