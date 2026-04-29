# Sudoku — Command-Line Game

A command-line Sudoku game written in Java. It generates a random puzzle each game, lets you place numbers, clear cells, ask for hints, and check for rule violations. The game ends when the grid is completely and correctly filled.

---

## Environment

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher
- **OS**: Windows, macOS, or Linux

---

## How to build and run

Navigate into the project folder first:

```bash
cd SudokuAssignment/sudoku
```

Build and run all tests:

```bash
mvn clean package
```

This produces a runnable JAR at `target/sudoku.jar`. Start the game with:

```bash
java -jar target/sudoku.jar
```

To run tests without rebuilding the JAR:

```bash
mvn test
```

---

## Gameplay

When the game starts, a 9x9 grid is shown with 30 pre-filled numbers. Empty cells are shown as `_`. Rows are labelled A–I, columns are numbered 1–9.

```
Welcome to Sudoku!

Here is your puzzle:
    1 2 3 4 5 6 7 8 9
  A 5 3 _ _ 7 _ _ _ _
  B 6 _ _ 1 9 5 _ _ _
  C _ 9 8 _ _ _ _ 6 _
  ...

Enter command (e.g., A3 4, C5 clear, hint, check, quit):
```

### Commands

| Command | What it does |
|---|---|
| `A3 4` | Place 4 in row A, column 3 |
| `C5 clear` | Remove your entry from row C, column 5 |
| `hint` | Reveal the correct value for one empty cell |
| `check` | Check the current grid for rule violations |
| `quit` | Exit the game |

The game ends when all 81 cells are correctly filled. You can choose to play again with a new puzzle.

---

## Design

### Package layout

```
com.sudoku
├── Main.java                  — entry point, wires everything together
├── model/
│   ├── SudokuGrid.java        — the 9x9 grid, tracks values and pre-filled state
│   └── ValidationResult.java  — result returned by the validator
├── command/
│   ├── Command.java           — sealed interface for all command types
│   ├── PlaceNumberCommand.java
│   ├── ClearCellCommand.java
│   ├── HintCommand.java
│   ├── CheckCommand.java
│   └── QuitCommand.java
├── service/
│   ├── CommandParser.java     — converts raw input strings into Command objects
│   ├── SudokuValidator.java   — checks rows, columns, and 3x3 subgrids for duplicates
│   ├── SudokuSolver.java      — backtracking solver, also used for hints and puzzle generation
│   └── PuzzleGenerator.java   — builds a random puzzle with exactly one valid solution
└── game/
    └── GameEngine.java        — the main game loop
```

### Design decisions worth mentioning

**Command pattern** — each user action is its own class (`PlaceNumberCommand`, `HintCommand`, etc.) rather than a string or enum. This keeps `GameEngine` clean and makes it easy to add new commands later without touching existing code.

**Dependency injection in `GameEngine`** — the engine accepts a `Scanner` and `PrintStream` through its constructor rather than calling `System.in` / `System.out` directly. This makes the game loop fully testable: tests inject scripted input and capture output from a byte stream.

**`SudokuSolver` does double duty** — the same backtracking solver is used by `PuzzleGenerator` to build a valid solution, by `PuzzleGenerator` again to verify uniqueness after each cell removal, and by `HintCommand` to look up the correct answer for empty cells.

**`ValidationResult` as a value object** — the validator returns a result object rather than printing or throwing. Caller decides what to do with the result, which makes unit testing straightforward.

### Assumptions

- Every generated puzzle has exactly one valid solution — `PuzzleGenerator` checks this before returning.
- Pre-filled cells are locked at the model level. There's no way to overwrite them regardless of how the game is driven.
- `check` reports the first violation found, checked in row → column → subgrid order.
- `hint` picks the first empty cell in reading order (left to right, top to bottom).
- Playing again generates a fresh puzzle rather than restarting the same one.

---

## Tests

| Test class | What it covers |
|---|---|
| `SudokuGridTest` | Cell values, pre-filled flag, bounds checking, deep copy protection |
| `CommandParserTest` | All valid commands, boundary values, bad input |
| `SudokuValidatorTest` | Row, column, and subgrid violation detection |
| `SudokuSolverTest` | Solving known puzzles, hint lookup, uniqueness checking |
| `PuzzleGeneratorTest` | Generated puzzles are valid, fully solved, and have a unique solution |
| `GameEngineTest` | End-to-end scenarios: valid move, pre-filled rejection, check, hint, quit |

Run all tests:

```bash
mvn test
```
