package com.sudoku.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// Generates a puzzle with 30 pre-filled cells and a guaranteed unique solution.
//
// How it works:
//   1. Fill a blank grid using backtracking with shuffled candidates (gives us a random valid solution).
//   2. Pick cells to remove in a random order.
//   3. Before committing each removal, check uniqueness — if removing the cell creates ambiguity, put it back.
//   4. Stop when 51 cells have been removed (30 remain).
public class PuzzleGenerator {

    private static final int TARGET_PRE_FILLED = 30;
    private static final int CELLS_TO_REMOVE = 81 - TARGET_PRE_FILLED;

    private final SudokuSolver solver;
    private final Random random;

    public PuzzleGenerator(SudokuSolver solver, Random random) {
        this.solver = solver;
        this.random = random;
    }

    // Returns [puzzle, solution] — puzzle has 0s for empty cells, solution is fully filled.
    public int[][][] generate() {
        int[][] solution = buildCompleteGrid();
        int[][] puzzle   = removeUntilTarget(solution);
        return new int[][][]{puzzle, solution};
    }

    private int[][] buildCompleteGrid() {
        int[][] grid = new int[9][9];
        fillGrid(grid);
        return grid;
    }

    private boolean fillGrid(int[][] grid) {
        int[] empty = findFirstEmpty(grid);
        if (empty == null) return true;

        int row = empty[0], col = empty[1];
        // Shuffle the candidates so we get a different grid each time
        List<Integer> candidates = shuffledDigits();
        for (int num : candidates) {
            if (solver.isPlacementValid(grid, row, col, num)) {
                grid[row][col] = num;
                if (fillGrid(grid)) return true;
                grid[row][col] = 0;
            }
        }
        return false;
    }

    private int[][] removeUntilTarget(int[][] solution) {
        int[][] puzzle = solver.deepCopy(solution);

        List<int[]> positions = allPositions();
        Collections.shuffle(positions, random);

        int removed = 0;
        for (int[] pos : positions) {
            if (removed >= CELLS_TO_REMOVE) break;

            int r = pos[0], c = pos[1];
            int backup = puzzle[r][c];
            puzzle[r][c] = 0;

            if (solver.hasUniqueSolution(puzzle)) {
                removed++;
            } else {
                puzzle[r][c] = backup; // removing this cell made the puzzle ambiguous, skip it
            }
        }
        return puzzle;
    }

    private List<int[]> allPositions() {
        List<int[]> positions = new ArrayList<>(81);
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                positions.add(new int[]{r, c});
            }
        }
        return positions;
    }

    private List<Integer> shuffledDigits() {
        List<Integer> digits = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(digits, random);
        return digits;
    }

    private int[] findFirstEmpty(int[][] grid) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (grid[r][c] == 0) return new int[]{r, c};
            }
        }
        return null;
    }
}
