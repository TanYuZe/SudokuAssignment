package com.sudoku.model;

// Simple value object so the validator can return a result without printing anything itself.
// Using static factories instead of a public constructor to make call sites more readable.
public final class ValidationResult {

    private final boolean valid;
    private final String message;

    private ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, "No rule violations detected.");
    }

    public static ValidationResult violation(String message) {
        return new ValidationResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
