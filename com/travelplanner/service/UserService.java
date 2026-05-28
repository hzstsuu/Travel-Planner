package com.travelplanner.service;
 
import com.travelplanner.model.User;
import com.travelplanner.repository.UserRepository;
 
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
 
public class UserService {
 
    private final UserRepository userRepository;
 
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
 
    // ── Registration ──────────────────────────────────────────────────────
 
    /**
     * Register a new user.
     * Throws IllegalArgumentException if the username is already taken.
     */
    public User register(String username, String password,
                         String fullName, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username \"" + username + "\" is already taken.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setJoinDate(LocalDate.now());
        return userRepository.save(user);
    }
 
    // ── Authentication ────────────────────────────────────────────────────
 
    /**
     * Authenticate a user by username and password.
     * Returns the User on success, empty Optional on wrong credentials.
     */
    public Optional<User> login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password));
    }
 
    // ── Forgot password ───────────────────────────────────────────────────
 
    /**
     * Reset a user's password after verifying their Full Name + Username.
     *
     * @param username    the account's username
     * @param fullName    the full name as originally registered (case-insensitive)
     * @param newPassword the new password to set
     * @return true on success; false if username not found or fullName does not match
     */
    public boolean resetPassword(String username, String fullName, String newPassword) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) return false;
 
        User user = opt.get();
        if (!user.getFullName().equalsIgnoreCase(fullName.trim())) return false;
 
        user.setPassword(newPassword);
        userRepository.update(user);
        return true;
    }
 
    // ── Delete Account ────────────────────────────────────────────────────
 
    /**
     * Permanently delete a user account by ID.
     * The caller is responsible for also deleting the user's trips,
     * expenses, itineraries, and travel logs beforehand.
     *
     * @param userId the numeric ID of the account to delete
     * @return true if the account was found and removed; false otherwise
     */
    public boolean deleteAccount(int userId) {
        return userRepository.delete(userId);
    }
 
    // ── Queries ───────────────────────────────────────────────────────────
 
    /**
     * Find a user by username (case-insensitive).
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
 
    /** Return all registered users (used by LeaderboardPanel). */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
 
    /** Find a user by their numeric ID. */
    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }
}