package com.sudoku.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleGeneratorTest {

    private PuzzleGenerator generator;
    private SudokuSolver solver;
    private SudokuValidator validator;

    @BeforeEach
    void setUp() {
        solver    = new SudokuSolver();
        validator = new SudokuValidator();
        generator = new PuzzleGenerator(solver, new Random(42)); // fixed seed so results are repeatable
    }

    @Test
    void generate_returnsTwoGrids() {
        int[][][] result = generator.generate();
        assertNotNull(result);
        assertEquals(2, result.length);
        assertNotNull(result[0]); // puzzle
        assertNotNull(result[1]); // solution
    }

    @Test
    void generate_puzzleIs9x9() {
        int[][] puzzle = generator.generate()[0];
        assertEquals(9, puzzle.length);
        for (int[] row : puzzle) assertEquals(9, row.length);
    }

    @Test
    void generate_solutionIsFullyFilled() {
        int[][] solution = generator.generate()[1];
        for (int[] row : solution) {
            for (int cell : row) {
                assertNotEquals(0, cell, "Solution must have no empty cells");
            }
        }
    }

    @Test
    void generate_solutionPassesValidation() {
        int[][] solution = generator.generate()[1];
        com.sudoku.model.SudokuGrid grid = new com.sudoku.model.SudokuGrid(solution);
        assertTrue(validator.validate(grid).isValid());
    }

    @Test
    void generate_puzzleHasAtLeast30PreFilledCells() {
        int[][] puzzle = generator.generate()[0];
        int count = 0;
        for (int[] row : puzzle) {
            for (int cell : row) {
                if (cell != 0) count++;
            }
        }
        // Could be slightly more than 30 if some removals had to be skipped to preserve uniqueness
        assertTrue(count >= 30, "Expected at least 30 pre-filled cells, got: " + count);
    }

    @Test
    void generate_puzzleHasUniqueSolution() {
        int[][] puzzle = generator.generate()[0];
        assertTrue(solver.hasUniqueSolution(puzzle));
    }

    @Test
    void generate_preFilledCellsMatchSolution() {
        int[][][] result = generator.generate();
        int[][] puzzle   = result[0];
        int[][] solution = result[1];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (puzzle[r][c] != 0) {
                    assertEquals(solution[r][c], puzzle[r][c],
                            "Pre-filled cell at [" + r + "][" + c + "] must match the solution");
                }
            }
        }
    }

    @RepeatedTest(3)
    void generate_calledMultipleTimes_producesValidPuzzles() {
        PuzzleGenerator gen = new PuzzleGenerator(solver, new Random());
        int[][][] result = gen.generate();
        assertTrue(solver.hasUniqueSolution(result[0]));
        assertTrue(validator.validate(new com.sudoku.model.SudokuGrid(result[1])).isValid());
    }
}
