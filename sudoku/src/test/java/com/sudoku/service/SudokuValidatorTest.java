package com.sudoku.service;

import com.sudoku.model.SudokuGrid;
import com.sudoku.model.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SudokuValidatorTest {

    private SudokuValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SudokuValidator();
    }

    // --- valid grids ---

    @Test
    void validate_emptyGrid_isValid() {
        SudokuGrid grid = new SudokuGrid(new int[9][9]);
        ValidationResult result = validator.validate(grid);
        assertTrue(result.isValid());
        assertEquals("No rule violations detected.", result.getMessage());
    }

    @Test
    void validate_correctlySolvedPuzzle_isValid() {
        int[][] solved = {
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
        assertTrue(validator.validate(new SudokuGrid(solved)).isValid());
    }

    @Test
    void validate_partiallyFilledWithNoConflicts_isValid() {
        int[][] puzzle = {
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
        assertTrue(validator.validate(new SudokuGrid(puzzle)).isValid());
    }

    // --- row violations ---

    @Test
    void validate_duplicateInRowA_reportsViolation() {
        // A2=3 is pre-filled; placing 3 at A3 creates a duplicate in row A
        int[][] puzzle = {
            {5, 3, 3, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        ValidationResult result = validator.validate(new SudokuGrid(puzzle));
        assertFalse(result.isValid());
        assertEquals("Number 3 already exists in Row A.", result.getMessage());
    }

    @Test
    void validate_duplicateInLastRow_reportsViolation() {
        int[][] puzzle = new int[9][9];
        puzzle[8][0] = 9;
        puzzle[8][4] = 9;
        ValidationResult result = validator.validate(new SudokuGrid(puzzle));
        assertFalse(result.isValid());
        assertEquals("Number 9 already exists in Row I.", result.getMessage());
    }

    // --- column violations ---

    @Test
    void validate_duplicateInColumn1_reportsViolation() {
        // A1=5 is pre-filled; C1=5 creates a column 1 duplicate
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {5, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        ValidationResult result = validator.validate(new SudokuGrid(puzzle));
        assertFalse(result.isValid());
        assertEquals("Number 5 already exists in Column 1.", result.getMessage());
    }

    @Test
    void validate_duplicateInColumn9_reportsViolation() {
        int[][] puzzle = new int[9][9];
        puzzle[0][8] = 7;
        puzzle[5][8] = 7;
        ValidationResult result = validator.validate(new SudokuGrid(puzzle));
        assertFalse(result.isValid());
        assertEquals("Number 7 already exists in Column 9.", result.getMessage());
    }

    // --- subgrid violations ---

    @Test
    void validate_duplicateInTopLeftSubgrid_reportsViolation() {
        // B2=8 conflicts with C3=8 in the top-left 3x3 box.
        // Using B2 (not B3) because B3 shares column 3 with C3,
        // which would trigger a column violation before the subgrid check.
        int[][] puzzle = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 8, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        ValidationResult result = validator.validate(new SudokuGrid(puzzle));
        assertFalse(result.isValid());
        assertEquals("Number 8 already exists in the same 3×3 subgrid.", result.getMessage());
    }

    @Test
    void validate_duplicateInBottomRightSubgrid_reportsViolation() {
        int[][] puzzle = new int[9][9];
        puzzle[6][6] = 4;
        puzzle[8][8] = 4;
        ValidationResult result = validator.validate(new SudokuGrid(puzzle));
        assertFalse(result.isValid());
        assertEquals("Number 4 already exists in the same 3×3 subgrid.", result.getMessage());
    }

    // --- empty cells should not trigger false positives ---

    @Test
    void validate_multipleEmptyCells_noFalsePositive() {
        SudokuGrid grid = new SudokuGrid(new int[9][9]);
        assertTrue(validator.validate(grid).isValid());
    }
}
