package com.sudoku.game;

import com.sudoku.command.*;
import com.sudoku.model.SudokuGrid;
import com.sudoku.model.ValidationResult;
import com.sudoku.service.CommandParser;
import com.sudoku.service.PuzzleGenerator;
import com.sudoku.service.SudokuSolver;
import com.sudoku.service.SudokuValidator;

import java.io.PrintStream;
import java.util.Scanner;

// Main game loop. Takes Scanner and PrintStream as constructor args so tests can
// inject fake input/output without touching System.in or System.out.
public class GameEngine {

    private final SudokuValidator validator;
    private final SudokuSolver solver;
    private final PuzzleGenerator generator;
    private final CommandParser parser;
    private final PrintStream out;

    public GameEngine(SudokuValidator validator,
                      SudokuSolver solver,
                      PuzzleGenerator generator,
                      CommandParser parser,
                      PrintStream out) {
        this.validator = validator;
        this.solver    = solver;
        this.generator = generator;
        this.parser    = parser;
        this.out       = out;
    }

    public void run(Scanner scanner) {
        out.println("Welcome to Sudoku!");

        boolean playAgain = true;
        while (playAgain) {
            int[][][] puzzleAndSolution = generator.generate();
            SudokuGrid grid    = new SudokuGrid(puzzleAndSolution[0]);
            int[][]   solution = puzzleAndSolution[1];

            out.println("\nHere is your puzzle:");
            printGrid(grid);

            boolean quit = playGame(grid, solution, scanner);
            if (quit) break;

            out.print("\nPress Enter to play again or type 'quit' to exit: ");
            String again = scanner.hasNextLine() ? scanner.nextLine() : "quit";
            playAgain = !again.trim().equalsIgnoreCase("quit");
        }

        out.println("\nThanks for playing. Goodbye!");
    }

    // Returns true if the player quit mid-game
    boolean playGame(SudokuGrid grid, int[][] solution, Scanner scanner) {
        while (!grid.isComplete()) {
            out.print("\nEnter command (e.g., A3 4, C5 clear, hint, check, quit): ");

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine();

            Command command;
            try {
                command = parser.parse(input);
            } catch (IllegalArgumentException e) {
                out.println("\nInvalid command: " + e.getMessage());
                out.println("\nCurrent grid:");
                printGrid(grid);
                continue;
            }

            if (command instanceof QuitCommand) {
                out.println("\nThanks for playing!");
                return true;
            } else if (command instanceof HintCommand) {
                handleHint(grid, solution);
                out.println("\nCurrent grid:");
                printGrid(grid);
            } else if (command instanceof CheckCommand) {
                handleCheck(grid);
            } else if (command instanceof PlaceNumberCommand place) {
                handlePlaceNumber(place, grid);
            } else if (command instanceof ClearCellCommand clear) {
                handleClearCell(clear, grid);
            }
        }

        // Grid is fully filled — do a final sanity check before declaring a win
        ValidationResult finalCheck = validator.validate(grid);
        if (finalCheck.isValid()) {
            out.println("\nCurrent grid:");
            printGrid(grid);
            out.println("\nYou have successfully completed the Sudoku puzzle!");
        } else {
            out.println("\nPuzzle is filled but has violations: " + finalCheck.getMessage());
        }
        return false;
    }

    private void handlePlaceNumber(PlaceNumberCommand cmd, SudokuGrid grid) {
        String cellName = cellName(cmd.row(), cmd.col());
        if (grid.isPreFilled(cmd.row(), cmd.col())) {
            out.println("\nInvalid move. " + cellName + " is pre-filled.");
            out.println("\nCurrent grid:");
            printGrid(grid);
            return;
        }
        grid.setCell(cmd.row(), cmd.col(), cmd.value());
        out.println("\nMove accepted.");
        out.println("\nCurrent grid:");
        printGrid(grid);
    }

    private void handleClearCell(ClearCellCommand cmd, SudokuGrid grid) {
        String cellName = cellName(cmd.row(), cmd.col());
        if (grid.isPreFilled(cmd.row(), cmd.col())) {
            out.println("\nInvalid move. " + cellName + " is pre-filled.");
            out.println("\nCurrent grid:");
            printGrid(grid);
            return;
        }
        grid.clearCell(cmd.row(), cmd.col());
        out.println("\nCell " + cellName + " cleared.");
        out.println("\nCurrent grid:");
        printGrid(grid);
    }

    private void handleHint(SudokuGrid grid, int[][] solution) {
        solver.getHint(grid, solution).ifPresentOrElse(
                hint -> {
                    String name = cellName(hint[0], hint[1]);
                    out.println("\nHint: Cell " + name + " = " + hint[2]);
                    grid.setCell(hint[0], hint[1], hint[2]);
                },
                () -> out.println("\nNo hints available — the puzzle is already complete!")
        );
    }

    private void handleCheck(SudokuGrid grid) {
        ValidationResult result = validator.validate(grid);
        out.println(result.getMessage());
    }

    void printGrid(SudokuGrid grid) {
        out.println("    1 2 3 4 5 6 7 8 9");
        for (int r = 0; r < 9; r++) {
            char rowChar = (char) ('A' + r);
            StringBuilder sb = new StringBuilder("  ").append(rowChar).append(" ");
            for (int c = 0; c < 9; c++) {
                int val = grid.getCell(r, c);
                sb.append(val == 0 ? "_" : val);
                if (c < 8) sb.append(" ");
            }
            out.println(sb);
        }
    }

    private String cellName(int row, int col) {
        return String.valueOf((char) ('A' + row)) + (col + 1);
    }
}
