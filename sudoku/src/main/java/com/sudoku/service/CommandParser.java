package com.sudoku.service;

import com.sudoku.command.*;

public class CommandParser {

    public Command parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        String trimmed = input.trim();
        String lower = trimmed.toLowerCase();

        switch (lower) {
            case "hint"  -> { return new HintCommand(); }
            case "check" -> { return new CheckCommand(); }
            case "quit"  -> { return new QuitCommand(); }
        }

        // Anything else should be two tokens: <cellRef> <value|clear>
        String[] parts = trimmed.split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException(
                    "Unknown command: \"" + trimmed + "\". " +
                    "Try something like: A3 4, C5 clear, hint, check, or quit.");
        }

        String cellRef = parts[0].toUpperCase();
        String action  = parts[1].toLowerCase();

        if (cellRef.length() != 2) {
            throw new IllegalArgumentException(
                    "Invalid cell \"" + cellRef + "\". Should be a letter (A-I) and a digit (1-9), e.g. A3.");
        }

        char rowChar = cellRef.charAt(0);
        if (rowChar < 'A' || rowChar > 'I') {
            throw new IllegalArgumentException(
                    "Row \"" + rowChar + "\" is not valid. Use A through I.");
        }
        int row = rowChar - 'A';

        char colChar = cellRef.charAt(1);
        if (colChar < '1' || colChar > '9') {
            throw new IllegalArgumentException(
                    "Column \"" + colChar + "\" is not valid. Use 1 through 9.");
        }
        int col = colChar - '1';

        if (action.equals("clear")) {
            return new ClearCellCommand(row, col);
        }

        int value;
        try {
            value = Integer.parseInt(action);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "\"" + action + "\" is not a valid action. Use a number 1-9 or \"clear\".");
        }

        if (value < 1 || value > 9) {
            throw new IllegalArgumentException(
                    value + " is out of range. Pick a number between 1 and 9.");
        }

        return new PlaceNumberCommand(row, col, value);
    }
}
