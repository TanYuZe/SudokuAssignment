package com.sudoku.service;

import com.sudoku.model.SudokuGrid;

import java.util.Optional;

// Standard recursive backtracking solver.
// Also used by PuzzleGenerator to verify a puzzle has exactly one solution.
public class SudokuSolver {

    // Returns a completed grid, or null if the puzzle has no solution.
    public int[][] solve(int[][] puzzle) {
        int[][] grid = deepCopy(puzzle);
        return backtrack(grid) ? grid : null;
    }

    // Finds the first empty cell (left to right, top to bottom) and returns the correct value from the solution.
    public Optional<int[]> getHint(SudokuGrid grid, int[][] solution) {
        int[][] cells = grid.getCells();
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (cells[r][c] == 0) {
                    return Optional.of(new int[]{r, c, solution[r][c]});
                }
            }
        }
        return Optional.empty();
    }

    // Used during puzzle generation to make sure we're not handing the player an ambiguous puzzle.
    public boolean hasUniqueSolution(int[][] puzzle) {
        int[][] grid = deepCopy(puzzle);
        int[] count = {0};
        countSolutions(grid, count);
        return count[0] == 1;
    }

    private boolean backtrack(int[][] grid) {
        int[] empty = findFirstEmpty(grid);
        if (empty == null) return true; // all cells filled, we're done

        int row = empty[0], col = empty[1];
        for (int num = 1; num <= 9; num++) {
            if (isPlacementValid(grid, row, col, num)) {
                grid[row][col] = num;
                if (backtrack(grid)) return true;
                grid[row][col] = 0; // didn't work out, undo and try next
            }
        }
        return false;
    }

    private void countSolutions(int[][] grid, int[] count) {
        if (count[0] > 1) return; // no need to keep searching once we know it's not unique

        int[] empty = findFirstEmpty(grid);
        if (empty == null) {
            count[0]++;
            return;
        }

        int row = empty[0], col = empty[1];
        for (int num = 1; num <= 9; num++) {
            if (isPlacementValid(grid, row, col, num)) {
                grid[row][col] = num;
                countSolutions(grid, count);
                grid[row][col] = 0;
            }
        }
    }

    private int[] findFirstEmpty(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0) return new int[]{r, c};
            }
        }
        return null;
    }

    // Check row, column, and the 3x3 box containing (row, col)
    boolean isPlacementValid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num) return false;
            if (grid[i][col] == num) return false;
        }
        // (row / 3) * 3 snaps to the top-left corner of the box
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if (grid[r][c] == num) return false;
            }
        }
        return true;
    }

    int[][] deepCopy(int[][] grid) {
        int[][] copy = new int[9][9];
        for (int r = 0; r < 9; r++) copy[r] = grid[r].clone();
        return copy;
    }
}
