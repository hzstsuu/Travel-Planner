package com.travelplanner.repository;

import com.travelplanner.model.User;

import java.time.LocalDate;
import java.util.Optional;

public class UserRepository extends FileRepository<User> {

    public UserRepository(String filePath) {
        super(filePath);
    }

    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    protected String serialize(User u) {
        return u.getId()
                + "|" + escape(u.getUsername())
                + "|" + escape(u.getPassword())
                + "|" + escape(u.getFullName())
                + "|" + escape(u.getEmail())
                + "|" + u.getJoinDate();
    }

    @Override
    protected User deserialize(String line) {
        String[] p = splitLine(line);
        return new User(
                Integer.parseInt(p[0]),
                unescape(p[1]),
                unescape(p[2]),
                unescape(p[3]),
                unescape(p[4]),
                LocalDate.parse(p[5])
        );
    }
}