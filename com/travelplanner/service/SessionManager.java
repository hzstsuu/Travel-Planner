package com.travelplanner.service;

import com.travelplanner.model.User;

/**
 * Lightweight singleton that holds the currently logged-in user.
 * UI panels call SessionManager.current() to get the active user.
 */
public final class SessionManager {
    private static User currentUser;

    private SessionManager() {}

    public static void login(User user)  { currentUser = user; }
    public static void logout()          { currentUser = null; }
    public static User current()         { return currentUser; }
    public static boolean isLoggedIn()   { return currentUser != null; }
    public static int currentUserId()    { return currentUser != null ? currentUser.getId() : 0; }
}