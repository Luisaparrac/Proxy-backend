package com.luisaparra.aiproxy.service;

import com.luisaparra.aiproxy.dto.UserSummaryResponse;
import com.luisaparra.aiproxy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Lists the demo users available in the platform.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserSummaryResponse> findAll() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(user -> user.getPlan().ordinal()))
                .map(user -> new UserSummaryResponse(user.getId(), user.getName(), user.getPlan()))
                .toList();
    }
}
