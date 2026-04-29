package com.sudoku.command;

public record ClearCellCommand(int row, int col) implements Command {
}
