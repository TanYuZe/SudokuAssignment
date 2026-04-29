package com.sudoku;

import com.sudoku.game.GameEngine;
import com.sudoku.service.*;

import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SudokuValidator validator = new SudokuValidator();
        SudokuSolver solver = new SudokuSolver();
        PuzzleGenerator generator = new PuzzleGenerator(solver, new Random());
        CommandParser parser = new CommandParser();

        GameEngine engine = new GameEngine(validator, solver, generator, parser, System.out);
        engine.run(new Scanner(System.in));
    }
}
