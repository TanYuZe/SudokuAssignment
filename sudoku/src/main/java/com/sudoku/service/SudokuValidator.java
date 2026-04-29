package com.sudoku.service;

import com.sudoku.model.SudokuGrid;
import com.sudoku.model.ValidationResult;

// Checks the three Sudoku rules: no duplicates in any row, column, or 3x3 box.
// Skips empty cells (value 0) so a partial grid can be validated mid-game.
public class SudokuValidator {

    public ValidationResult validate(SudokuGrid grid) {
        int[][] cells = grid.getCells();

        for (int r = 0; r < 9; r++) {
            ValidationResult result = checkRow(cells, r);
            if (!result.isValid()) return result;
        }

        for (int c = 0; c < 9; c++) {
            ValidationResult result = checkColumn(cells, c);
            if (!result.isValid()) return result;
        }

        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                ValidationResult result = checkSubgrid(cells, boxRow * 3, boxCol * 3);
                if (!result.isValid()) return result;
            }
        }

        return ValidationResult.ok();
    }

    private ValidationResult checkRow(int[][] cells, int row) {
        boolean[] seen = new boolean[10];
        for (int c = 0; c < 9; c++) {
            int val = cells[row][c];
            if (val == 0) continue;
            if (seen[val]) {
                char rowLabel = (char) ('A' + row);
                return ValidationResult.violation("Number " + val + " already exists in Row " + rowLabel + ".");
            }
            seen[val] = true;
        }
        return ValidationResult.ok();
    }

    private ValidationResult checkColumn(int[][] cells, int col) {
        boolean[] seen = new boolean[10];
        for (int r = 0; r < 9; r++) {
            int val = cells[r][col];
            if (val == 0) continue;
            if (seen[val]) {
                return ValidationResult.violation("Number " + val + " already exists in Column " + (col + 1) + ".");
            }
            seen[val] = true;
        }
        return ValidationResult.ok();
    }

    private ValidationResult checkSubgrid(int[][] cells, int startRow, int startCol) {
        boolean[] seen = new boolean[10];
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                int val = cells[r][c];
                if (val == 0) continue;
                if (seen[val]) {
                    return ValidationResult.violation("Number " + val + " already exists in the same 3×3 subgrid.");
                }
                seen[val] = true;
            }
        }
        return ValidationResult.ok();
    }
}
