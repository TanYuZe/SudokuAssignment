package com.sudoku.model;

public class SudokuGrid {

    private static final int SIZE = 9;

    private final int[][] cells;
    private final boolean[][] preFilledCells;

    public SudokuGrid(int[][] initialPuzzle) {
        if (initialPuzzle == null || initialPuzzle.length != SIZE) {
            throw new IllegalArgumentException("Puzzle must be a 9x9 grid");
        }
        cells = new int[SIZE][SIZE];
        preFilledCells = new boolean[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            if (initialPuzzle[r].length != SIZE) {
                throw new IllegalArgumentException("Each row must have 9 columns");
            }
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = initialPuzzle[r][c];
                preFilledCells[r][c] = initialPuzzle[r][c] != 0;
            }
        }
    }

    public void setCell(int row, int col, int value) {
        validateBounds(row, col);
        if (preFilledCells[row][col]) {
            throw new IllegalStateException("Cannot modify pre-filled cell at " + cellName(row, col));
        }
        if (value < 1 || value > 9) {
            throw new IllegalArgumentException("Value must be between 1 and 9, got: " + value);
        }
        cells[row][col] = value;
    }

    public void clearCell(int row, int col) {
        validateBounds(row, col);
        if (preFilledCells[row][col]) {
            throw new IllegalStateException("Cannot clear pre-filled cell at " + cellName(row, col));
        }
        cells[row][col] = 0;
    }

    public int getCell(int row, int col) {
        validateBounds(row, col);
        return cells[row][col];
    }

    public boolean isPreFilled(int row, int col) {
        validateBounds(row, col);
        return preFilledCells[row][col];
    }

    public boolean isComplete() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] == 0) return false;
            }
        }
        return true;
    }

    // Returns a deep copy so callers can't reach in and modify our internal state
    public int[][] getCells() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            copy[r] = cells[r].clone();
        }
        return copy;
    }

    public int countPreFilled() {
        int count = 0;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (preFilledCells[r][c]) count++;
            }
        }
        return count;
    }

    private void validateBounds(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException("Cell (" + row + "," + col + ") is out of bounds");
        }
    }

    // Converts 0-indexed row/col back to the player-facing name e.g. (0, 2) -> "A3"
    private String cellName(int row, int col) {
        return String.valueOf((char) ('A' + row)) + (col + 1);
    }
}
