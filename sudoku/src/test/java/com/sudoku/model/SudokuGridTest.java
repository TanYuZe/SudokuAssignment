package com.sudoku.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SudokuGridTest {

    // The example puzzle from the spec — 30 pre-filled cells
    private static final int[][] SAMPLE_PUZZLE = {
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

    private SudokuGrid grid;

    @BeforeEach
    void setUp() {
        grid = new SudokuGrid(SAMPLE_PUZZLE);
    }

    // --- construction ---

    @Test
    void constructor_nullPuzzle_throws() {
        assertThrows(IllegalArgumentException.class, () -> new SudokuGrid(null));
    }

    @Test
    void constructor_wrongRowCount_throws() {
        assertThrows(IllegalArgumentException.class, () -> new SudokuGrid(new int[8][9]));
    }

    @Test
    void constructor_wrongColCount_throws() {
        int[][] bad = new int[9][];
        for (int r = 0; r < 9; r++) bad[r] = new int[r == 3 ? 8 : 9];
        assertThrows(IllegalArgumentException.class, () -> new SudokuGrid(bad));
    }

    // --- pre-filled detection ---

    @Test
    void isPreFilled_nonZeroCell_returnsTrue() {
        assertTrue(grid.isPreFilled(0, 0)); // A1 = 5
        assertTrue(grid.isPreFilled(0, 1)); // A2 = 3
        assertTrue(grid.isPreFilled(0, 4)); // A5 = 7
    }

    @Test
    void isPreFilled_zeroCell_returnsFalse() {
        assertFalse(grid.isPreFilled(0, 2)); // A3 = empty
        assertFalse(grid.isPreFilled(0, 3)); // A4 = empty
    }

    @Test
    void countPreFilled_returnsExpectedCount() {
        assertEquals(30, grid.countPreFilled());
    }

    // --- getCell ---

    @Test
    void getCell_preFilledValue_correct() {
        assertEquals(5, grid.getCell(0, 0));
        assertEquals(9, grid.getCell(2, 1));
    }

    @Test
    void getCell_emptyCell_returnsZero() {
        assertEquals(0, grid.getCell(0, 2));
    }

    @Test
    void getCell_outOfBounds_throws() {
        assertThrows(IllegalArgumentException.class, () -> grid.getCell(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> grid.getCell(0, 9));
        assertThrows(IllegalArgumentException.class, () -> grid.getCell(9, 0));
    }

    // --- setCell ---

    @Test
    void setCell_emptyCell_succeeds() {
        grid.setCell(0, 2, 4); // place 4 at A3
        assertEquals(4, grid.getCell(0, 2));
    }

    @Test
    void setCell_preFilledCell_throwsIllegalState() {
        assertThrows(IllegalStateException.class, () -> grid.setCell(0, 0, 9));
    }

    @Test
    void setCell_valueBelowRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> grid.setCell(0, 2, 0));
    }

    @Test
    void setCell_valueAboveRange_throws() {
        assertThrows(IllegalArgumentException.class, () -> grid.setCell(0, 2, 10));
    }

    @Test
    void setCell_overwriteUserEntry_succeeds() {
        grid.setCell(0, 2, 4);
        grid.setCell(0, 2, 6);
        assertEquals(6, grid.getCell(0, 2));
    }

    // --- clearCell ---

    @Test
    void clearCell_userFilledCell_becomesZero() {
        grid.setCell(0, 2, 4);
        grid.clearCell(0, 2);
        assertEquals(0, grid.getCell(0, 2));
    }

    @Test
    void clearCell_preFilledCell_throwsIllegalState() {
        assertThrows(IllegalStateException.class, () -> grid.clearCell(0, 0));
    }

    @Test
    void clearCell_alreadyEmptyCell_succeeds() {
        assertDoesNotThrow(() -> grid.clearCell(0, 2));
        assertEquals(0, grid.getCell(0, 2));
    }

    // --- isComplete ---

    @Test
    void isComplete_puzzleWithEmptyCells_returnsFalse() {
        assertFalse(grid.isComplete());
    }

    @Test
    void isComplete_fullyFilledGrid_returnsTrue() {
        int[][] full = {
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
        assertTrue(new SudokuGrid(full).isComplete());
    }

    @Test
    void isComplete_singleEmptyCell_returnsFalse() {
        int[][] almostFull = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 0}  // last cell empty
        };
        assertFalse(new SudokuGrid(almostFull).isComplete());
    }

    // --- getCells returns a deep copy ---

    @Test
    void getCells_modifyingCopy_doesNotAffectGrid() {
        int[][] copy = grid.getCells();
        copy[0][2] = 99;
        assertEquals(0, grid.getCell(0, 2)); // original should be untouched
    }
}
