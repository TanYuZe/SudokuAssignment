package com.sudoku.command;

// Sealed so the compiler knows every possible command type.
// No accidental "catch-all" cases in the game loop.
public sealed interface Command
        permits PlaceNumberCommand, ClearCellCommand, HintCommand, CheckCommand, QuitCommand {
}
