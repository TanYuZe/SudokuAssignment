package com.sudoku.command;

// row and col are 0-indexed internally; the parser handles the conversion from A-I / 1-9
public record PlaceNumberCommand(int row, int col, int value) implements Command {
}
