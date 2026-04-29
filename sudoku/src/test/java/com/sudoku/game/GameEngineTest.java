package com.sudoku.game;

import com.sudoku.model.SudokuGrid;
import com.sudoku.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GameEngine.
 * Input is driven by a string passed to Scanner; output is captured via ByteArrayOutputStream.
 */
class GameEngineTest {

    /** The example puzzle from the problem statement. */
    private static final int[][] PUZZLE = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private static final int[][] SOLUTION = {
        {5, 3, 4, 6, 7, 8, 9, 1, 2},
        {6, 7, 2, 1, 9, 5, 3, 4, 8},
        {1, 9, 8, 3, 4, 2, 5, 6, 7},
        {8, 5, 9, 7, 6, 1, 4, 2, 3},
        {4, 2, 6, 8, 5, 3, 7, 9, 1},
        {7, 1, 3, 9, 2, 4, 8, 5, 6},
        {9, 6, 1, 5, 3, 7, 2, 8, 4},
        {2, 8, 7, 4, 1, 9, 6, 3, 5},
        {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    private ByteArrayOutputStream outStream;
    private PrintStream out;
    private SudokuValidator validator;
    private SudokuSolver solver;
    private CommandParser parser;

    @BeforeEach
    void setUp() {
        outStream = new ByteArrayOutputStream();
        out       = new PrintStream(outStream, true, StandardCharsets.UTF_8);
        validator = new SudokuValidator();
        solver    = new SudokuSolver();
        parser    = new CommandParser();
    }

    /** Builds an engine for tests — generator is null because we call playGame() directly. */
    private GameEngine engineWithFixedPuzzle() {
        return new GameEngine(validator, solver, null, parser, out);
    }

    private String runGame(String commands) {
        GameEngine engine = engineWithFixedPuzzle();
        Scanner scanner = new Scanner(commands);
        engine.playGame(new SudokuGrid(PUZZLE), SOLUTION, scanner);
        return outStream.toString(StandardCharsets.UTF_8);
    }

    // --- valid move ---

    @Test
    void placeNumber_validMove_outputsMoveAccepted() {
        String output = runGame("A3 4\nquit\n");
        assertTrue(output.contains("Move accepted."));
    }

    @Test
    void placeNumber_validMove_updatesGridDisplay() {
        String output = runGame("A3 4\nquit\n");
        // Grid should now show 4 at position A3
        assertTrue(output.contains("5 3 4"));
    }

    // --- pre-filled cell protection ---

    @Test
    void placeNumber_preFilledCell_showsErrorMessage() {
        String output = runGame("A1 6\nquit\n");
        assertTrue(output.contains("A1 is pre-filled"));
    }

    @Test
    void placeNumber_preFilledCell_gridUnchanged() {
        String output = runGame("A1 6\nquit\n");
        // A1 should still be 5, not 6
        assertFalse(output.contains("6 3 _")); // would appear if A1 changed to 6
    }

    // --- clear ---

    @Test
    void clearCell_userFilledCell_clearsAndConfirms() {
        String output = runGame("A3 4\nA3 clear\nquit\n");
        assertTrue(output.contains("A3 cleared."));
    }

    @Test
    void clearCell_preFilledCell_showsErrorMessage() {
        String output = runGame("A1 clear\nquit\n");
        assertTrue(output.contains("A1 is pre-filled"));
    }

    // --- check ---

    @Test
    void check_noViolations_reportsOk() {
        String output = runGame("check\nquit\n");
        assertTrue(output.contains("No rule violations detected."));
    }

    @Test
    void check_rowViolation_reportsRowMessage() {
        // A3=3 duplicates A2=3 → row A violation
        String output = runGame("A3 3\ncheck\nquit\n");
        assertTrue(output.contains("Number 3 already exists in Row A."));
    }

    @Test
    void check_columnViolation_reportsColumnMessage() {
        // C1=5 duplicates A1=5 → column 1 violation
        String output = runGame("C1 5\ncheck\nquit\n");
        assertTrue(output.contains("Number 5 already exists in Column 1."));
    }

    @Test
    void check_subgridViolation_reportsSubgridMessage() {
        // B2=8 conflicts with C3=8 in the top-left 3x3 box.
        // Using B2 (not B3) because B3 shares column 3 with C3,
        // which would trigger a column violation before the subgrid check.
        String output = runGame("B2 8\ncheck\nquit\n");
        assertTrue(output.contains("Number 8 already exists in the same 3×3 subgrid."));
    }

    // --- hint ---

    @Test
    void hint_partialGrid_revealsFirstEmptyCell() {
        String output = runGame("hint\nquit\n");
        // First empty cell is A3 = 4 in the solution
        assertTrue(output.contains("Hint: Cell A3 = 4"));
    }

    @Test
    void hint_placesNumberInGrid() {
        String output = runGame("hint\nquit\n");
        // After hint the grid should show 4 at A3
        assertTrue(output.contains("5 3 4"));
    }

    // --- quit ---

    @Test
    void quit_midGame_endsImmediately() {
        String output = runGame("quit\n");
        assertTrue(output.contains("Thanks for playing!"));
        assertFalse(output.contains("You have successfully completed"));
    }

    // --- invalid command ---

    @Test
    void invalidCommand_showsErrorAndGrid() {
        String output = runGame("foobar\nquit\n");
        assertTrue(output.contains("Invalid command:"));
        assertTrue(output.contains("Current grid:"));
    }

    @Test
    void emptyCommand_showsErrorAndGrid() {
        String output = runGame("\nquit\n");
        assertTrue(output.contains("Invalid command:"));
    }

    // --- winning condition ---

    @Test
    void completePuzzle_correctSolution_showsVictoryMessage() {
        // Build all correct moves: fill every empty cell in order
        StringBuilder moves = buildWinningMoves();
        String output = runGame(moves.toString());
        assertTrue(output.contains("You have successfully completed the Sudoku puzzle!"));
    }

    @Test
    void gridDisplay_format_matchesSpec() {
        // A valid move triggers a grid print; check the display matches the spec format
        String output = runGame("A3 4\nquit\n");
        assertTrue(output.contains("    1 2 3 4 5 6 7 8 9"));
        assertTrue(output.contains("  A "));
        assertTrue(output.contains("  I "));
    }

    // --- helper ---

    private StringBuilder buildWinningMoves() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (PUZZLE[r][c] == 0) {
                    char row = (char) ('A' + r);
                    int col   = c + 1;
                    int val   = SOLUTION[r][c];
                    sb.append(row).append(col).append(" ").append(val).append("\n");
                }
            }
        }
        return sb;
    }
}
