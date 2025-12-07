package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class UserErrors {
    public static final Error USER_EXISTS = new Error(
            "User.UserExists", "User with entered email already exists.");

    public static final Error INVALID_CONFIRMATION_CODE = new Error(
            "User.InvalidConfirmationCode", "Invalid or expired confirmation code.");

    public static final Error EMAIL_NOT_FOUND = new Error(
            "User.EmailNotFound", "Email not found.");

    public static final Error USER_NOT_FOUND = new Error(
            "User.UserNotFound", "User not found.");

    public static final Error EMAIL_CONFIRMATION_NOT_FOUND = new Error(
            "User.EmailConfirmationNotFound", "Error finding email confirmation.");

    public static final Error INCORRECT_PASSWORD = new Error(
            "User.IncorrectPassword", "Incorrect password.");

    public static final Error NO_ROLES_SPECIFIED = new Error(
            "User.NoRolesSpecified", "At least one role must be specified.");

    public static final Error INVALID_ROLES = new Error(
            "User.InvalidRoles", "Invalid role(s).");
}