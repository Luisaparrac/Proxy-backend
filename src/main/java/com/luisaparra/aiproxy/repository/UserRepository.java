package com.luisaparra.aiproxy.repository;

import com.luisaparra.aiproxy.model.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage of user accounts.
 */
@Repository
public class UserRepository {

    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();

    public UserAccount save(UserAccount user) {
        users.put(user.getId(), user);
        return user;
    }

    public Optional<UserAccount> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<UserAccount> findAll() {
        return users.values();
    }
}
